# Github Desktop Notifications

You need to add an environment variable `GITHUB_TOKEN` for the application to work, the token has to be valid of course
for the app to work, and has to have access to the repos in the watch list.

## Extra Step on Mac
You need to do this after you define your `GITHUB_TOKEN` :
```shell
launchctl setenv GITHUB_TOKEN $GITHUB_TOKEN
```

You add the repos you would like to watch:

![Main-App-Window.png](images/Main-App-Window.png)

You get notifications whenever there is a new PR in one of the watched repos

![Notification.png](images/Notification.png)

If you happen to miss some notification, or you want to see the currently open PRs, hit <kbd>Ctrl</kbd> + <kbd>O</kbd> or click `Open PRs` button, to see the list:

![Open-PRs.png](images%2FOpen-PRs.png)

## Keymap

| Key                                                                    | Action                                        |
|------------------------------------------------------------------------|-----------------------------------------------|
| <kbd>Ctrl</kbd> + <kbd>ENTER</kbd> <br/> <kbd>ALT</kbd> + <kbd>A</kbd> | Insert new watched repo in the watch list     |
| <kbd>Ctrl</kbd> + <kbd>O</kbd> <br/> <kbd>ALT</kbd> + <kbd>O</kbd>     | Open a list of currently open PRs             |
| <kbd>Ctrl</kbd> + <kbd>D</kbd> <br/> <kbd>ALT</kbd> + <kbd>R</kbd>     | Removes the selected repo from the watch list |
| <kbd>Ctrl</kbd> + <kbd>S</kbd>                                         | Saves the repos watch list to disk            |
| <kbd>F1</kbd>  <br/> <kbd>ALT</kbd> + <kbd>H</kbd>                     | Show help message                             |
| <kbd>ALT</kbd> + <kbd>N</kbd>                                          | Focus the repo name text box                  |
| <kbd>ALT</kbd> + <kbd>W</kbd>                                          | Focus the repos list                          |
