# Scala Play RESTful API

This project is a RESTful API built with Scala and the Play Framework. It provides endpoints to interact with Google Books API, handle book data, and store it in MongoDB. The API supports operations for retrieving, adding, and updating book records.

## Running the project

**Clone the Repository:**
   ```sh
   git clone https://github.com/Tayamul/GoogleBooks.git
   cd GoogleBooks
   ```
**Setup Environment:**

`Ensure you have Java JDK 8+ installed.`

`Install SBT (Scala Build Tool).`

**Configuration:**

`Update application.conf with your MongoDB connection details and any other required configurations.`

**Run the Application:**

```sh
sbt run
```

**Access the Application:**

`Open a browser and navigate to http://localhost:9000 to view the API and interact with it.`

## Technologies

- **Play Framework** - A reactive web application framework for Java and Scala.
- **Scala** - A powerful language that combines object-oriented and functional programming.
- **MongoDB** - A NoSQL database used for storing book records.
- **Google Books API** - Used for fetching book data.

## Languages

- **Scala** - Primary language used for development.
- **HTML/CSS** - For creating and styling web forms and views.

## Features

- **Google Books Integration**: Retrieve book details using ISBN from Google Books API.
- **MongoDB Integration**: Store and manage book records using MongoDB
- **Form Handling**: Add and update book records through HTML forms.
- **Error Handling**: Comprehensive error handling and user feedback.

## Tests

- **Testing Framework**: The project uses ScalaTest for unit and integration testing. Ensure tests are run to verify application functionality.

---

## License

MIT License

Copyright (c) [2024]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files, to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---




