# Product Requirements

## 1. Product Summary

- Product name: Fertilizer Shop Manager
- One-line description: A store-management app for running fertilizer sales, stock, customer dues, and daily billing from one dashboard.
- Problem being solved: Fertilizer shops often track stock, customer balances, and sales manually in notebooks or spreadsheets, which causes stock mistakes, missed payments, and poor reporting.
- Target users: Fertilizer shop owner, shop staff/cashier, and later an accountant/admin role if needed.

## 2. User Goals

- Quickly create bills while serving walk-in customers.
- Track available fertilizer stock accurately.
- Record customer purchases and pending dues.
- Check daily sales and low-stock items without manual calculation.
- Reduce errors caused by paper-based records.

## 3. Core Features

- Feature 1: Product and inventory management
  - User action: Owner/staff adds fertilizer products, pack sizes, prices, stock quantity, and supplier details.
  - System response: App stores products and updates available inventory after each sale or manual stock adjustment.
  - Success condition: Staff can always see current stock and identify low-stock items.

- Feature 2: Billing and sales recording
  - User action: Staff creates a bill, selects products, enters quantity, discount, payment status, and payment mode.
  - System response: App calculates totals, saves the sale, updates stock, and records whether the bill is paid or due.
  - Success condition: A sale can be completed in less than 1 minute with correct totals and stock deduction.

- Feature 3: Customer ledger and dues tracking
  - User action: Staff selects or creates a customer when selling on credit, then records payment collection later.
  - System response: App maintains customer purchase history, pending balance, and payment entries.
  - Success condition: Owner can see each customer’s outstanding due and payment history anytime.

- Feature 4: Dashboard and reports
  - User action: Owner opens dashboard or reports page.
  - System response: App shows daily sales summary, unpaid dues, top-selling products, and low-stock alerts.
  - Success condition: Owner can understand shop performance in under 2 minutes.

## 4. MVP Scope

Include only what is necessary for version 1.

### In Scope

- Login for admin/store owner
- Product CRUD
- Inventory quantity tracking
- Customer CRUD
- Sales bill creation
- Paid vs due sale handling
- Customer payment collection entry
- Dashboard summary
- Low-stock alerts
- Basic sales and due reports

### Out of Scope

- Online payments
- GST/tax automation beyond simple configurable fields
- Supplier purchase order workflow
- Multi-store support
- SMS/WhatsApp notifications
- Barcode scanning
- Native mobile app
- Advanced analytics

## 5. User Roles

- Admin/Owner: Full access to products, inventory, customers, sales, reports, and settings.
- Staff/Cashier: Can create sales, view products/customers, and record customer payments, with limited settings access.

## 6. Functional Requirements

- The system must support secure login for at least the owner account.
- The system must allow creation, editing, archiving, and viewing of fertilizer products.
- The system must track stock by product and adjust it on every sale or stock update.
- The system must allow sales with multiple line items in a single bill.
- The system must support cash, UPI, card, and credit/due payment types.
- The system must create a customer record for credit sales.
- The system must track each customer’s outstanding balance and payment history.
- The system must show daily, weekly, and custom-date sales summaries.
- The system must show low-stock products based on a configurable threshold.
- The system must store timestamps and the user who created/updated important records.

## 7. Non-Functional Requirements

- Performance target: common screens should load in under 3 seconds on standard broadband.
- Security needs: authenticated access, hashed passwords, validated inputs, and protected admin endpoints.
- Expected scale: suitable for one store initially, with room for a few thousand products and many daily sales records.
- Device support: desktop-first, tablet-friendly for store counter use.
- Browser support: latest Chrome and Edge as primary targets.
- Backup/recovery expectations: database backup process should be possible daily.
- Reliability target: no loss of finalized sales records during normal use.

## 8. Success Metrics

- Staff can create a bill in under 1 minute.
- Owner can find a customer’s due balance in under 30 seconds.
- Low-stock items are visible without checking notebooks manually.
- Daily sales total matches recorded bills consistently.
- Critical user flows work without manual database edits.

## 9. Open Questions

- Do you need support for product batches or expiry dates for fertilizer stock?
- Do you want simple local deployment first or cloud deployment later?
- Should bills include printable invoice output in MVP or later?
- Do you need support for regional language labels in the UI?

## 10. Approval

- Product direction agreed: initial fertilizer shop management app
- MVP agreed: inventory, billing, customer dues, and dashboard
- First release agreed: single-store web app
