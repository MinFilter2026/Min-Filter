# Min Filter 1.5.2 — GitHub phone upload guide

## Upload the project

Upload the contents of this project folder to the repository root. Do not upload the ZIP itself.

## Enable GitHub Actions from an Android phone

Android file managers may hide `.github`, so the repository may not receive the workflow automatically.

Use GitHub's web interface:

**Add file → Create new file**

File name:

`.github/workflows/build-apk.yml`

Copy the contents of:

`GITHUB_ACTIONS/build-apk.yml`

Then **Commit changes**.

After that:

**Actions → Build Min Filter APK → Run workflow**

The APK will be available under the workflow's **Artifacts** when the build succeeds.
