# Integrate Wearables Challenge

# What

Implement WHOOP wearable integration

- Breathing rate
- Oxygen Saturation
- **Heart Rate**
- Heart Rate Variability - HRV
- **Blood Pressure**

Or - Integrate Apple Watch

*Or - Sync Heart Rate from Oura in the YOU(th) App*

Business logic requirements:

We need to develop the system which support features:
1. User data collection from Junction service – User should be able to connect his whoop / apple watch / devices to the Junction, and we need to get user data from it.**(the data is sent 100 request per minute, and expected to be sent more).**
2. From the app side user should be able to fetch his data (in order to display it on a timeline chart)

Tech requirements:
FastAPI / Any Database (except for sqlite3) / Docker
1. Ensure you create a scalable solution, which allows to connect a lot of devices and the system can work with thousands of users
2. Create a scalable, readable and well organized project infrastructure, please use all good practices you know
3. Tests are required

Think about:
1. how would you deploy the system and where to host
2. how the system should behave under high load and scale

- Extra notes:
    - Develop a service collect data from Junction integration
    - We should be able to fetch Junction —> Fetch, process, and Save —> Get data and send us data. Create API to send data and we use postman
    - Basic UI

# Deliverables

- [ ]  Focus on codebase / project infrastructure  —> Github (modules, layers)
    - Create a scalable, readable and well organized project infrastructure, please use all good practices you know
- [ ]  Present your wearable integration MVP
    - [ ]  How did you build it
    - [ ]  How did you structure code (see also point above)
    - [ ]  Quick system diagram
- [ ]  Build or explain how to build tests
- [ ]  For the future: Suggest system architecture, services, and how to deploy at scale
    - [ ]  Draw an ideal system diagram —> Draw.io
    - [ ]  How to scale the system and deployment as the number of users increases (e.g. 10k - now, 50k, 1M, 50M)

# Why

- Features requested by 80%+ of users interviewed

Plus, we want to test you with one of the features we have been challenged the most.

# Metrics impacted

- Retention
- Perceived Trust

# Materials

- Figma with implementation: https://www.figma.com/design/51L7Y4QQDVcP8GuXotUrgt/YOU-th--app?node-id=8923-630295&t=cUpMqiqheYwylqev-0
- App Access: Download Youth Health Hub from the app store
    - Click YES when asked if you are coming from a partner
    - Select youth demo
    - Add the code: WorldChanger-VIP
- Junction - Our wearable provider
    - Documentation:  https://docs.junction.com/wearables/providers/resources
    - Credentials:
        - **Junction**

            email: services@youth-healthtech.com

            Login via code on the email

        - Sandbox Junction
        - OR Separate team on Junction
- Previous epics implemented:
    - Implement wearables integration
    - ‣
    - ‣
- Potential solution
    - Microservice, create a separate service standalone, takes request from Junction, and send request to them, and app can handle data points from the junction
        - Connect with our app
            - API endpoint
            - We can then call the endpoint
        - Connect with Junction
        - Deploy
    - Question:
        - How much data expect?
        - How to handle?

# Expected behavior

Key flows (see Figma and current app):

- Wearable sync onboarding Flow
    - Triggered from:
        - Welcome prompt
        - + button
        - systems setting
- Wearable sync connection success feedback:
    - Success - In progress
    - Failed - Try again
- Wearable results update & display
    - Inform users results are ready and show Daily insights results flow
    - Display information in data sections
- Wearable sync - Sync issues

    When we stop receiving data for any reasons

- Wearable sync - Disconnect flow

- PREVIOUS EPICS - EXPECTED BEHAVIOR / USER FLOW (note: test current app to see current behvior)
    - User finishes onboarding and access the Home Section (NEW USER) OR Users opens the app after app update (EXISTING USER)
    - User sees the wearable integration Welcome prompt: ‣
    - User clicks “Not Now”
        - Wearable integration TO DO goes in the Home Feed - Home Feed Card reminding the users to sync wearables: ‣
            - User clicks X → Home Feed Prompt disappears and does not appear again. User will need to go into the profile section to sync a wearable ‣
            - User clicks Connect a Device CTA —> ***User accesses the wearable integration*** flow
    - User clicks Connect a Device —> ***User accesses the wearable integration flow***
    - Once the user accesses the wearable integration flow
        - User selects the wearable they want to integrate from the menu: ‣ The menu has the following wearables at the beginning:
            - Oura
            - Whoop
            - Apple Watch
            - Garmin
        - We show 2 statuses:
            - Connection expired
            - Not connected
            - We do not show already connected here
        - User clicks on a specific device
        - We show intro screen: ‣
        - User clicks continues - User is redirected to integration flow from Juction (or Terra, or Rook)
        - Once process is completed, user sees the success screen: ‣
            - Note: success screen differs for different devices
            - Clicking continue redirects users to the profile section, so they see the connected device ‣
        - If any errors occur during the connection, we show the error prompts: ‣
    - If connection expires, we show banner on the home, and we retrigger specific wearable flow upon clicking the connect CTA
        - ‣
    - In the profile, we now see the Devices section.
        - If no device is connected: ‣
        - if some devices are connected or expired:  ‣
        - User can disconnect a connected device / reconnect an expired device from the profile:  ‣
            - If user disconnect a device, device disappears from the profile section and it becomes available from the menu when users click connect a device CTA

# Other requirements

- Focus on codebase / project infrastructure  —> Github (modules, layers)
- Suggest services and how to deploy at scale —> System diagrams (also on words)
- Draw an ideal system dyagram —> Draw.io
- How to scale the system as the number of users increases

# Q&A

Feel free to ask any questions, collect missing information, spar on approaches, asked us for any credentials or materials.

“Use me”, spar with me, ask my help, challenge me as much as you want

- Nice-to-to-have - Extra questions from our side
    - What are the biggest criticalities and edge cases you see?
    - What would you improve in the current implementation?
    - Other coming during the task, to avoid biasing you too much on the direction
