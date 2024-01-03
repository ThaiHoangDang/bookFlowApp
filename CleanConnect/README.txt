Student Id: s3925997Student Name: Le Trinh Quoc HuynhFunctionality of the App:1. Map Fragment:   - Display a Google Map with markers representing cleanup sites.2. Filtering and Sorting:   - Implement a dialog fragment to filter and sort cleanup sites by distance, title, etc.   - Use a RecyclerView with a ViewModel to display the filtered and sorted list.3. Firebase Integration:   - Utilize Firebase for user authentication and site data storage.   - Implement a Firestore database for storing cleanup site details.4. Notifications:   - Send notifications when a cleanup site is updated using Firebase Cloud Messaging (FCM).5. User Profile:
   - 2 different user types: SUPER & USER   - Include a user profile fragment with details like email, display name, etc.   - Save and retrieve user tokens for seamless login.6. Location Services:   - Implement location-related features, such as displaying the user's current location on the map.   - Find & draw route to a location   - Allow users to create new cleanup sites by searching for an address with auto-suggestion.Technology Use:- Android Development with Java- Google Maps SDK for Android- Firebase Authentication for user authentication- Firestore for storing cleanup site data
- Firebase Storage for storing media- Firebase Cloud Messaging (FCM) for notifications- ViewModel and LiveData for managing UI-related data- Google Places API for address auto-suggestion- Firebase Cloud Functions for automated notifications
- Material3 for front-end developmentDrawbacks:- Potential limitations with the Google Places API (e.g., usage quotas, billing).- Handling asynchronous tasks (e.g., fetching user details) could impact UI responsiveness.Test accounts:
SUPER user: 
- email: letrinhquoch@gmail.com
- password: 24122003

Normal user:
- email: test1@gmail.com
- password: 1234567890