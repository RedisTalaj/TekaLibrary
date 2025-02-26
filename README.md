# Teka Library
TekaLibrary is a library management system designed to help users manage books, authors, and categories efficiently. It provides features for borrowing books, adding reviews, managing user accounts, and more. Built with Spring Boot and Thymeleaf, this project is a full-stack application that includes both backend and frontend components.

#Features
- Book Management
Add Books: Add new books with details like title, author, category, description, ISBN, quantity, price, and an optional image.
Update Books: Modify existing book details, including uploading a new image.
Delete Books: Remove books from the library.
Search Books: Search for books by title or author.
Book Details: View detailed information about a book, including reviews and availability.
Loan Books: Users can loan books, and the system tracks loan dates and return dates.
Categories: Books are organized into categories (e.g., Comedy, Drama, Fiction, Horror, Non-Fiction).


- User Management
User Registration: Users can sign up with email, password, and other details.
User Login: Secure login system with password validation.
User Roles: Two roles are supported - Admin and User.
Admins can manage users and books.
Users can loan books, add reviews, and manage their profiles.
User Profile: Users can view and edit their profiles, including changing their password.
Favorite Books: Users can add or remove books from their favorites list.
Order History: Users can view their loan history.

- Reviews
Add Reviews: Users can submit reviews for books, including a rating, title, and comment.
Delete Reviews: Users can delete their reviews.

- Admin Features
Manage Users: Admins can create, update, and delete users.
Manage Books: Admins can add, update, and delete books.
Search Users: Admins can search for users by username.

- Frontend Features
Responsive Design: The application is designed to work on both desktop and mobile devices.
Dynamic Content: The homepage displays the newest books and books by category.
Image Upload: Books can include images, which are stored locally and displayed in the UI.

#Technologies Used
- Backend
Spring Boot: For building the backend application.
Spring Data JPA: For database interactions.
Thymeleaf: For server-side rendering of HTML templates.
MySQL Workbench: For fast data storage and retrieval.

- Frontend
HTML/CSS/JavaScript: For the user interface.
Bootstrap: For styling and responsive design.

- Database
MySQL: For storing application data (e.g., books, users, reviews).

- Other Tools
Maven: For dependency management.
Git: For version control.
