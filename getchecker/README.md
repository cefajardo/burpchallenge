# GetCheckerExtension

The `GetCheckerExtension` is a Burp Suite extension that logs HTTP requests and responses, and checks if a POST request can be converted to a GET request with the same parameters. It initializes the extension, registers HTTP handlers, and sets up a user interface tab for logging issues detected during the HTTP request/response cycle.

## Features

- Logs HTTP requests and responses.
- Checks if a POST request can be converted to a GET request with the same parameters.
- Provides a user interface tab for logging issues.

## Installation

1. Download the JAR file of the extension.
2. Open Burp Suite.
3. Go to the "Extender" tab.
4. Click on the "Add" button.
5. Select the downloaded JAR file.

## Usage

Once the extension is installed, it will automatically start logging HTTP requests and responses. If a POST request is detected, it will attempt to convert it to a GET request with the same parameters and log any issues detected.