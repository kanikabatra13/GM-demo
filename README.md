# 🚗 Digital Product Subscription System (General Motors – Technical Assignment)

This backend system simulates a **SaaS-style model** for managing digital products purchased by organizations (e.g., fleets), with support for product fulfillment, subscription lifecycle management, and vehicle-level activation.

---

## ✅ Features Implemented

### 📦 Product Catalog
- Global catalog of digital products (e.g., OnStar, Fleet Maintenance)
- Product types: `ONE_TIME`, `TERM`, `RENEWABLE`
- Configurable subscription term (e.g., 1 year)

### 🏢 Organization-Product Subscriptions
- Organizations **subscribe to products** by initiating a purchase
- Subscriptions are created in `PENDING` state and become `ACTIVE` upon confirmation
- Purchase is **simulated via callback** from an external (mocked) payment microservice
- Support for **manual and auto-renewal** of subscriptions

### 🚘 Vehicle-Level Assignments
- Vehicles belong to organizations
- Vehicles can be assigned to **active organization-level product subscriptions**
- Assignments inherit expiry from the org subscription
- Assignment status: `ACTIVE`, `CANCELLED`, `EXPIRED`

### 🔁 Subscription Lifecycle Management
- **Manual renewal** endpoint for organization subscriptions
- **Automatic renewal scheduler** for `RENEWABLE` products nearing expiry
- Vehicle assignments are extended automatically upon renewal

### 🛒 Purchase Simulation
- Mock purchase service:
    - Introduces random delay (simulating async processing)
    - Randomly succeeds or fails
    - Sends a `POST` callback to confirm or reject the purchase
- Handles `PENDING → ACTIVE` or `CANCELLED` transition

### 📊 Organization Summary & Reporting
- Summary endpoint returns:
    - Each product an organization is subscribed to
    - Number of vehicles assigned to each subscription
- Supports filtering by:
    - Subscription status (`ACTIVE`, `CANCELLED`, etc.)
    - Assignment status (`ACTIVE`, `EXPIRED`, etc.)

### 🧪 Test Coverage
- Integration tests for:
    - Purchase initiation
    - Callback processing
    - Product assignment to vehicles
    - Full end-to-end subscription + activation flow

---

## 🔧 Tech Stack

- **Spring Boot**
- **MongoDB** (Docker-based)
- **Spring Data MongoDB**
- **MockMvc** (Spring Boot Test)
- **Spring Scheduler** (`@Scheduled`) for auto-renewal
- **Lombok** for boilerplate reduction

---

## 🚀 To Do Next (Optional Enhancements)

- Add audit/history tracking for renewals
- Notification system for auto-renewals
- Limit-based licensing (e.g., max vehicles per subscription)
- Retry mechanism for failed purchase callbacks

## 🛠️ Installation & Setup

### 🔧 Prerequisites
- Java 17+
- Gradle (or use `./gradlew`)
- MongoDB (local or Docker-based)

---

### 🚀 Run with Local MongoDB

Start MongoDB using Docker:

```bash
docker run --name local-mongo -p 27017:27017 -d mongo




