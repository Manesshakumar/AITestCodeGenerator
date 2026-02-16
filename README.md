# AI-Powered Selenium Test Generator



## Overview
This project automaticall![GIFTestcode](https://github.com/user-attachments/assets/092d63d7-9e36-40dc-a9cc-651fa4aa4915)
y generates **Selenium + TestNG test classes** in Java from user stories using the **Gemini-2.5-Flash** AI model. The generated tests include:

- Screenshots on test failure with attachment
- Clear and maintainable test code with proper **TestNG assertions**.
- One `@Test` method per logical test case.

---

## How It Works
1. **Write a User Story**  
   Define your user story with acceptance criteria. Example:

   ```text
   As a registered user,
   I want to log in to the website with valid credentials
   so that I can access my dashboard.

   Acceptance Criteria:
   - Navigate to https://opensource-demo.orangehrmlive.com/
   - Verify the Page Title
   - Enter valid username and password
   - Verify successful login by checking dashboard visibility or Page Title
