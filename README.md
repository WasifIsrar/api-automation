# Project Folder Structure

This document provides an overview of the folder structure for this framework. It outlines the purpose of each directory and highlights key files for navigation and usage.

## Folder Structure

```plaintext
MobileApp_UIAutomation/
├── .github/
│   └── workflows/           # CI/CD workflows for GitHub Actions                
├── scripts/                 # Utility and setup scripts
├── src/                     # Source code for the application and tests
│   └── test/                # Test code for Appium
│       ├── java/            # Java test classes
│       │   ├── endpoints/   # API Endpoints and Methods
│       │   ├── payload/     # POJO Classes and payload json files
│       │   └── reporting/   # Reporting Utility for Tests Execution
│       │   └── resources/   # Properties file to store data
│       │   └── test/        # Test Classes containing Tests
│       │   └── utilities/   # Token Managers and other common utilities
├── test-suites/             # Testng.xml Suites
├── pom.xml                  # Maven build configuration file
└── README.md                # Project documentation
