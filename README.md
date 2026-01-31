# 💬 Distributed Java Chat Application

A **fault-tolerant, coordinator-based distributed chat system** built in **Java** using socket programming, multithreading, and software design patterns.  
Developed as part of the **COMP1549 – Advanced Programming** module at the **University of Greenwich**.

---

## 📌 Project Overview

This project implements a **networked client–server chat application** that enables multiple clients to communicate reliably within a group.  
The system uses a **coordinator-based architecture**, where one client acts as the coordinator and maintains group state. If the coordinator disconnects, a **new coordinator is automatically elected** to ensure uninterrupted communication.

The application supports both **private and broadcast messaging** and is designed to tolerate unexpected client failures.

---

## ✨ Key Features

- 🔗 Client–Server architecture using Java sockets  
- 👑 Coordinator-based group management  
- 🔄 Automatic leader (coordinator) reassignment  
- 📢 Broadcast messaging to all connected clients  
- 🔒 Private one-to-one messaging  
- 🧠 Duplicate client ID prevention  
- ♻️ Fault tolerance for client and coordinator failures  
- ⏱ Periodic health checks to maintain active group state  

---

## 🧱 Architecture & Design Patterns

The project applies established **software design patterns** to improve modularity, maintainability, and reliability:

- **Singleton Pattern**  
  Ensures a single server instance manages shared global state.

- **Factory Pattern**  
  Decouples client handler creation from server logic, improving extensibility.

- **Observer Pattern**  
  Supports message notification and event-based communication.

---

## 🧪 Testing (JUnit)

JUnit test cases were implemented to validate key system behaviour, including:

- Coordinator selection and reassignment  
- Private message routing  
- Broadcast message delivery  
- Group membership consistency after disconnections  

Mock client handlers were used to test logic independently of real network connections.

---

## ⚙️ Technologies & Tools

- Java  
- Socket Programming  
- Multithreading  
- JUnit  
- Software Design Patterns  
- Git & GitHub  

---

## ▶️ How to Run (CLI-based)

1. Start the server  
2. Launch multiple clients with unique IDs  
3. Send private or broadcast messages  
4. Disconnect clients to observe coordinator reassignment  

---

## 🚀 Future Enhancements

- GUI-based client interface  
- Automatic client reconnection  
- End-to-end message encryption  
- Distributed servers with load balancing  

---

## 📌 Author

**Hamza Ali Khan**  
🎓 BSc (Hons) Computer Science with Artificial Intelligence  
🏫 University of Greenwich  
🔗 LinkedIn: https://www.linkedin.com/in/hamza-ali-khan-69116820b/
