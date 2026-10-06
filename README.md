# GitHub Actions — easy setup from a phone

GitHub only runs Actions workflows from `.github/workflows/`. Some Android file managers hide folders beginning with `.`. Therefore this folder contains a visible copy of the workflow.

## If you upload this project from an Android phone

1. Upload the project files/folders to the repository.
2. In GitHub, choose **Add file → Create new file**.
3. Set the file path to:
   `.github/workflows/build-apk.yml`
4. Open the visible file `GITHUB_ACTIONS/build-apk.yml` from this project and copy its contents into the new GitHub file.
5. Commit the file.
6. Open **Actions → Build Min Filter APK** and run the workflow.

This is a GitHub requirement, not a Min Filter limitation: Actions does not execute workflow files kept in an ordinary visible folder.
