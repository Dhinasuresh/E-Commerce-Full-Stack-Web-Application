# Architecture Notes

This project uses:

- Frontend: React + Vite
- Backend: Spring Boot REST API
- Database: MySQL

## 1. High-Level Design

- Client application in `Front-End`
- REST API in `BackEnd/todo-app`
- Persistent storage in MySQL
- Single-store internal web app for owner and staff

Core flow:

1. User logs in
2. Frontend calls REST API
3. API validates request and performs business logic
4. Data is stored/retrieved from MySQL
5. Dashboard and reports are derived from sales, customer, and product data

## 2. Core Domain Model

- User
  - Purpose: login and authorization
  - Important fields: id, name, username/email, passwordHash, role, status
  - Relationships: creates sales and payment entries

- Product
  - Purpose: represent fertilizer items sold in the shop
  - Important fields: id, name, brand, category, sku, packSize, unit, salePrice, purchasePrice, stockQty, lowStockThreshold, active
  - Relationships: linked to sale items and stock adjustments

- Customer
  - Purpose: track regular customers and credit sales
  - Important fields: id, name, phone, villageOrAddress, notes, currentDue, active
  - Relationships: linked to sales and customer payments

- Sale
  - Purpose: store bill-level transaction data
  - Important fields: id, billNumber, customerId, saleDate, subtotal, discount, grandTotal, paidAmount, dueAmount, paymentStatus, paymentMethod, createdBy
  - Relationships: has many sale items, belongs to a customer optionally, created by user

- SaleItem
  - Purpose: store line items inside a bill
  - Important fields: id, saleId, productId, quantity, unitPrice, lineTotal
  - Relationships: belongs to sale and product

- CustomerPayment
  - Purpose: record payments collected against previous dues
  - Important fields: id, customerId, amount, paymentDate, paymentMethod, note, recordedBy
  - Relationships: belongs to customer and user

- StockAdjustment
  - Purpose: track manual stock changes
  - Important fields: id, productId, adjustmentType, quantity, reason, createdBy, createdAt
  - Relationships: belongs to product and user

## 3. API Design

### Authentication

- `POST /api/auth/login`: authenticate owner/staff
- `GET /api/auth/me`: fetch current logged-in user

### Products

- `GET /api/products`: list products with filters
- `POST /api/products`: create product
- `GET /api/products/{id}`: get product details
- `PUT /api/products/{id}`: update product
- `PATCH /api/products/{id}/stock`: adjust stock manually

### Customers

- `GET /api/customers`: list customers
- `POST /api/customers`: create customer
- `GET /api/customers/{id}`: get customer profile with due info
- `PUT /api/customers/{id}`: update customer
- `GET /api/customers/{id}/ledger`: fetch sales and payment history
- `POST /api/customers/{id}/payments`: record payment collection

### Sales

- `GET /api/sales`: list sales with date and status filters
- `POST /api/sales`: create a new bill
- `GET /api/sales/{id}`: get bill details

### Dashboard and Reports

- `GET /api/dashboard/summary`: totals for sales, dues, and low-stock count
- `GET /api/reports/sales`: sales report by date range
- `GET /api/reports/dues`: unpaid dues report
- `GET /api/reports/low-stock`: low-stock products

For implementation:

- Use DTOs for requests/responses
- Validate required fields in backend
- Return clear error messages for invalid stock, invalid payment, or missing customer/product

## 4. Database Design

- `users`
  - id, name, username, password_hash, role, status, created_at, updated_at

- `products`
  - id, name, brand, category, sku, pack_size, unit, purchase_price, sale_price, stock_qty, low_stock_threshold, active, created_at, updated_at

- `customers`
  - id, name, phone, village_or_address, notes, current_due, active, created_at, updated_at

- `sales`
  - id, bill_number, customer_id nullable, sale_date, subtotal, discount, grand_total, paid_amount, due_amount, payment_status, payment_method, created_by, created_at

- `sale_items`
  - id, sale_id, product_id, quantity, unit_price, line_total

- `customer_payments`
  - id, customer_id, amount, payment_date, payment_method, note, recorded_by, created_at

- `stock_adjustments`
  - id, product_id, adjustment_type, quantity, reason, created_by, created_at

Design decisions:

- Use hard delete rarely; prefer `active` status/soft archive for products and customers
- Keep timestamps on all main tables
- Add indexes on `bill_number`, `sale_date`, `customer_id`, `product_id`, and `phone`

## 5. Frontend Design

- `/login`
  - Purpose: authenticate owner/staff
  - Components: login form

- `/dashboard`
  - Purpose: summary of today’s sales, dues, and low stock
  - Components: summary cards, low-stock list, recent sales table

- `/products`
  - Purpose: manage fertilizer items
  - Components: product table, filters, add/edit form, stock adjust modal

- `/customers`
  - Purpose: manage customer records
  - Components: customer list, create/edit form, due summary

- `/customers/:id`
  - Purpose: customer ledger view
  - Components: profile card, due balance, sales history, payments list, collect payment form

- `/sales/new`
  - Purpose: create bill
  - Components: customer selector, product selector, bill items table, totals card, payment section

- `/sales`
  - Purpose: view sales history
  - Components: date filter, status filter, sales table

- `/reports`
  - Purpose: reporting
  - Components: tabs for sales, dues, low stock

Frontend rules:

- Handle loading, empty, and error states for all data screens
- Keep billing flow fast and keyboard-friendly where possible

## 6. Security

- Authentication method: session or JWT-based login; JWT is acceptable for current architecture
- Authorization rules: admin full access, staff limited access
- Password handling: bcrypt/secure password hashing
- Input validation: backend validation required for all writable endpoints
- CORS policy: allow only frontend origin in production
- Secrets management: keep DB credentials and JWT secret in environment variables only

## 7. Operational Design

- Local development setup
  - Frontend runs with Vite dev server
  - Backend runs with Spring Boot
  - MySQL runs locally

- Environment variables
  - frontend API base URL
  - backend DB URL, DB username, DB password
  - JWT secret

- Build process
  - Frontend: `npm run build`
  - Backend: `mvnw clean package`

- Deployment target
  - First release can be a local-office or single-server deployment
  - Later can move to cloud VPS

- Logging approach
  - backend request and error logging
  - avoid logging secrets or passwords

- Monitoring approach
  - start with application logs and daily manual review

## 8. Risks

- Technical risk: incorrect stock calculations if sale creation is not transaction-safe
- Scaling risk: reports may become slower if not indexed well
- Delivery risk: requirements may expand into accounting features too early
- UX risk: billing screen may become slow if product search is not designed well

## 9. Decisions

- Date: 2026-04-06
- Decision: Build a single-store fertilizer shop web app as MVP
- Reason: fastest path to a usable product for real daily operations
- Impact: focus first on inventory, billing, dues, and reporting instead of advanced accounting
