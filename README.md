Project Title: Expense Splitting App

Team Information
	1.	Team Members:
	•	Duong Duc Thinh - s3979652
	•	Khuu Thien Quang - s3979034
	•	Nguyen Minh Quan - s3975128
	2.	Work Distribution:
	•	Quang: Responsible for implementing all transaction-related functionalities:
		Pay, Request, Top Up, Withdraw and History.
		Updating the UI for transaction management transaction history and user interface.
	•	Thinh: Worked on user-related features:
	  Sign Up, Sign In, and user account information management.
		Contacts: Adding and managing user contacts.
		Updating the UI for user authentication and contact management.
	•	Quan: Focused on group-related features:
	  Implementing group activities such as group expense splitting, group management, and group transaction history.
Functionalities
	•	Core Functionalities:
	1.	Transaction Management:
	•	Users can perform Top Up, Withdraw, Pay, and Request transactions.
	•	Transaction history is displayed with filtering options (All, Pay, Request, Top Up, Withdraw).
	2.	Wallet Balance Management:
	•	Updates wallet balances in real-time after transactions.
	3.	User Authentication:
	•	Integrated Firebase Authentication for secure login and signup.
	•	Advanced Features:
	1.	Card Type Detection:
	•	Automatically detects Visa or MasterCard based on card number and displays the corresponding icon.
	2.	Dynamic Filtering:
	•	Tab-based filters for viewing specific types of transactions.
	3.	Offline Mode:
	•	Allows users to view cached transaction history even without an internet connection.

Technology Stack
	•	Programming Language(s): Java
	•	Framework(s): Android SDK
	•	Database(s): Firebase Firestore, SQLite
	•	Other Tools/Technologies: Firebase Authentication, Google Material Design, Glide (for image handling)

Open Issues
	1.	Performance: App performance is slightly slower when loading large transaction histories.
	2.	Notification System: Notifications for successful transactions are not yet implemented.
	3.	Date Parsing: A known issue with parsing timestamps from Firestore into Date objects.

Known Bugs
	1.	[Bug 1]:
	•	Description: App crashes if the timestamp in Firestore is stored as a String instead of a Date.
	•	Steps to Reproduce:
	1.	Add a transaction in Firestore with a String timestamp.
	2.	Open the app and navigate to the transaction history.
	•	Possible Cause: Incorrect data type mapping in the Transaction model.
	•	Workaround: Ensure that all timestamps are stored as Firestore Timestamp objects.
	2.	[Bug 2]:
	•	Description: The UI for the withdraw receipt occasionally overlaps with the “Confirm” button on small screen devices.
	•	Steps to Reproduce:
	1.	Perform a withdrawal on a device with a screen size <5 inches.
	•	Possible Cause: Constraints issue in the layout XML file.

Future Improvements
	1.	Implement a notification system for transactions.
	2.	Add support for multi-currency transactions.
	3.	Enhance performance optimization for large datasets.
