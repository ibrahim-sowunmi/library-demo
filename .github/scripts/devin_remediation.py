import asyncio
import aiohttp
import os
from datetime import datetime

# Environment variables
GITHUB_REPOSITORY = os.getenv("GITHUB_REPOSITORY")
SONAR_TOKEN = os.getenv("SONAR_TOKEN")
DEVIN_API_KEY = os.getenv("DEVIN_API_KEY")
SONAR_ORG = os.getenv("SONAR_ORG")
SONAR_PROJECT_KEY = os.getenv("SONAR_PROJECT_KEY")
DEVIN_API_BASE = "https://api.devin.ai/v1"

async def get_sonarcloud_issues():
    """Fetch open vulnerabilities from SonarCloud."""
    url = "https://sonarcloud.io/api/issues/search"
    headers = {"Authorization": f"Bearer {SONAR_TOKEN}"}
    params = {
        "organization": SONAR_ORG,
        "projectKeys": SONAR_PROJECT_KEY,
        "types": "VULNERABILITY",
        "statuses": "OPEN"
    }

    async with aiohttp.ClientSession() as session:
        async with session.get(url, headers=headers, params=params) as response:
            if response.status != 200:
                print(f"Error getting SonarCloud issues: {await response.text()}")
                return []
            result = await response.json()
            print(f"Found {len(result.get('issues', []))} issues")
            return result.get('issues', [])

async def delegate_task_to_devin(issue):
    """Delegate the entire task of fixing, committing, and pushing to Devin AI."""
    async with aiohttp.ClientSession() as session:
        headers = {"Authorization": f"Bearer {DEVIN_API_KEY}"}
        prompt = f"""
        Fix the following vulnerability in {GITHUB_REPOSITORY}: {issue['message']} in file {issue['component']}.
        1. Create a new branch named 'devin/{issue['key']}-fix-vulnerability'.
        2. Implement the fix.
        3. Write a detailed commit message explaining the changes:
            - Issue Key: {issue['key']}
            - Component: {issue['component']}
            - Fixed by Devin AI at {datetime.now().isoformat()}
            - Include 'Co-authored-by: github-actions[bot] <github-actions[bot]@users.noreply.github.com>'.
        4. Push the branch to the remote repository.
        5. Open a pull request with a description of the fix.
        """

        data = {"prompt": prompt, "idempotent": True}

        async with session.post(f"{DEVIN_API_BASE}/sessions", json=data, headers=headers) as response:
            if response.status != 200:
                print(f"Error delegating task to Devin: {await response.text()}")
                return None
            result = await response.json()
            print(f"Devin session created: {result}")
            return result

async def monitor_devin_session(session_id):
    """Monitor Devin's progress until it completes the task."""
    async with aiohttp.ClientSession() as session:
        headers = {"Authorization": f"Bearer {DEVIN_API_KEY}"}

        while True:
            async with session.get(f"{DEVIN_API_BASE}/session/{session_id}", headers=headers) as response:
                if response.status != 200:
                    print(f"Error monitoring Devin session: {await response.text()}")
                    return None

                result = await response.json()
                status = result.get("status_enum")

                if status in ["completed", "stopped"]:
                    print(f"Devin completed the task: {result}")
                    return result
                elif status == "blocked":
                    print("Devin encountered an issue. Please check manually.")
                    return None

                await asyncio.sleep(5)

async def main():
    try:
        issues = await get_sonarcloud_issues()

        for issue in issues:
            print(f"Processing issue: {issue['key']}")

            # Delegate task to Devin AI
            session_data = await delegate_task_to_devin(issue)

            if session_data:
                session_id = session_data["session_id"]

                # Monitor Devin's progress
                await monitor_devin_session(session_id)

    except Exception as e:
        print(f"Error occurred: {str(e)}")
        raise

if __name__ == "__main__":
    asyncio.run(main())