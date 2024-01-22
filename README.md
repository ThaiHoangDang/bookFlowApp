>Last edited: 2024-01-22 16:34:37 +07:00

# BookFlow
>A social media app for book lovers

![Android Development](https://github.com/ThaiHoangDang/bookFlowApp/blob/main/public/images/courseBanner.png?raw=true)


## I. Description

BookFlow is a social media haven for bookworms! Whether you're devouring thrilling mysteries, escaping in fantasy worlds, or diving into historical depths, BookFlow acts as a central hub for all your reading needs. The platform connects you with other passionate readers to share, discuss, and discover your next literary adventure. BookFlow isn't just an app, it's a community.

![Design](https://github.com/ThaiHoangDang/bookFlowApp/blob/main/public/images/design.png?raw=true)

## II. Functionalities

### Book-centric activities

`Book Detail page`: Provide detailed information about a particular book with information like name, author, genre, description, and overall rating.

`Review books`: Users have the ability to share their opinions and insights by posting reviews for books available on the platform. Users can assign a rating (on the scale of 1 to 5 stars) to the books they review and write down their thoughts about the book.

`Review Detail page`: Provide detailed information about a particular review with information like review owner, title, content, etc. Users can engage with a review with actions like Like or Comment.

`Book suggestions`: it is important to note that there is a difference between the book “suggestion” (this) and “identification” (below). The suggestion feature takes a photo as an input, sends it to Gemini API, and outputs the recommended book(s) that we thought you might like. For instances:

- You are in a good mood today, you took a selfie and our system can recommend you books on Self-help, Motivation, etc.
- The photo could be a picture of your dog, sure enough, the system can recommend books on Veterinary and Pet Care.
- You are learning chess, thus, you take a photo of your chess board, sit back and let our system recommend you books on Chess.

`Book identification`: imaging you are walking down the street and saw a fascinating book. You use this feature to take a photo of it and our system will extract any text that is the book metadata (titles, authors, genres, publishers, published date, etc.) This can be saved to your Notes or copied and searched in our app. You can now review it, add it to your favorite list, or share it with your friends seamlessly with just a few clicks. No typing needed.

### User-centric activities

`Authentication system`: Users can login or sign up for a new account. Two different login options are provided: using Email & Password and using Google account.

`News feed`: Displays all reviews made on our platform. Users are provided two sorting options: by chronological order (meaning from newest to oldest) and personal preference (based on the user’s favorite books and following users). Think of the newsfeed like a priority queue. When user *favorite* a book, the next time they visit the news feed, that book will be on top of the list.

`Verified account`: Users may purchase $10.99 with a credit card to get their account verified. Upon successful verification, users will receive a distinguished blue tick badge displayed beside their name. This badge serves as a visual indicator of a verified account.

`User-following system`: Users have the ability to follow each other within the platform, creating a social network where connections are built based on shared interests, content, or relationships. Users will receive notifications whenever a user they follow posts a new review. The notification includes the title and the book image of the review.

`Book Timer (Android Service)`: Users can use the Book Timer feature to start personalized reading timers for individual books in their collection. This allows users to track and record the cumulative time spent reading on each book. The Book Timer displays real-time progress, showing users the total elapsed time since they started reading a particular book. Users can easily check the progress of their reading sessions.

### Real-time messaging

`Chat System`: Users can engage in real-time conversations with each other through a built-in chat system. The platform also facilitates direct communication between users and the system administrator through the chat system (Customer Service). Users can seek assistance, report issues, or inquire about specific matters by initiating a chat with the admin. Admins can provide support, guidance, or address user concerns promptly through the messaging interface. Users receive real-time notifications for new messages, ensuring that they stay informed and can respond promptly to incoming messages.

`Video call`: Users can engage in face-to face conversations through the integrated video call feature. The platform provides a seamless and high-quality video call experience, allowing users to connect to each other in real time. Users have the ability to initiate one-on-one video calls with other users on the platform.

### System administrations

`Admin`: An admin account is provided to provide customer support and monitor the reviews made by normal users. Admin can take swift action and delete any inappropriate reviews.


## III. Additional information

Demo accounts:
- User:
  - Email: guest@gmail.com
  - Password: 123456
- Admin:
  - Email: admin@admin.com
  - Password: admin123

Per the Canvas requirements:

- `Database persistent`: we used Firebase
- `Services`: A book timer functionality that allows users to track their reading time even when the application is closed
- `Broadcast receivers`: Notifications are sent to users whenever they receive a new message or a user they follow post a new review.

Technologies:

- `Java and XML`
- `Firebase Authentication`: Handle login and sign up of users
- `Firebase Firestore`: A realtime database management system
- `Firebase Storage`: Storing static assets like images
- `Firebase Messaging`: A cloud service for messages and notifications
- `Cloud Functions`: Respond to events from Google Cloud services
- `Stripe`: Provide payment API
- `Zegocloud`: Provide video call API
- `Google DeepMind state-of-the-art Gemini LLM`: We specifically used the Google Gemini Pro Vision model to process multimodal inputs (text and image). User requests are handled with Reactive Streams (asynchronous stream processing with non-blocking back pressure). See https://www.reactive-streams.org/


## IV. Open issues and existing bugs

- The video chat may stop working if a participant's name includes special characters or only numbers (e.g.: ‘123456’ or ‘Huynh Quốc’). In other words, we need users from both side to have alphabet-only username (1-9, special characters, foreign languages, and so forth are forbidden)
- Notifications may delay as the cloud functions server is based in the U.S.
- Cannot make real payments with Stripe as Vietnam is not supported on Stripe.
- Real-time chat does not have data encryption.

## V. Members

- Le Trinh Quoc Huynh (s3923997): 25%
- Nguyen Trong Thanh (s3818993): 25%
- Dao Anh Vu (s3926187): 25%
- Dang Thai Hoang (s3927234): 25%

## VI. Resources

- GitHub repository: https://github.com/ThaiHoangDang/bookFlowApp
- Jira Scrum Board: https://rmit-s3925997.atlassian.net/browse/BFA
- Firebase: https://console.firebase.google.com/u/0/project/striking-water-408603/overview

## VII. License
- [MIT License](LICENSE)

---

Thank you for having a look at our project ❤️