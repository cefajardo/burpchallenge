# Security Test Project - Burp Challenge

This repository contains various security testing tools and extensions developed for the Security Test. The tools are designed to enhance security testing capabilities within Burp Suite by providing additional functionalities such as scanning for Personally Identifiable Information (PII) and checking for potential security issues in HTTP requests and responses.

## Table of Contents
 - [PII Scanner Extension](#pii-scanner-extension)
 - [Get Checker Extension](#get-checker-extension)
 - [Burp Challenge WebSite](#burp-challenge-website)


## PII Scanner Extension

The `PII Scanner Extension` is a Burp Suite extension that scans HTTP responses for Personally Identifiable Information (PII), specifically CPF patterns, and logs any detected issues.

### Features

- Scans HTTP responses for CPF patterns.
- Logs detected CPF patterns as issues.
- Provides a user interface tab for viewing logged issues.

## Get Checker Extension

The `Get Checker Extension` is a Burp Suite extension that logs HTTP requests and responses, and checks if a POST request can be converted to a GET request with the same parameters. It logs any issues detected during the HTTP request/response cycle.

### Features

- Logs HTTP requests and responses.
- Checks if a POST request can be converted to a GET request with the same parameters.
- Provides a user interface tab for logging issues.

## Burp Challenge WebSite

This is a simple Flask application used to test the extension implemented for Burp Challenge test.

### Features

- Generates a random CPF-like number.
- Displays a form with a select input and two buttons.
- Handles form submissions with different actions based on the button clicked.
- Displays a message based on the selected option.
- For the Not Forget Route enable the POST and GET Method
