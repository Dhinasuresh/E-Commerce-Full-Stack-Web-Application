# Roadmap

Break work into milestones. Each milestone should produce something testable.

## Phase 1: Discovery and Finalization

- Confirm MVP for fertilizer shop operations
- Confirm whether bills need printable invoice support in MVP
- Confirm whether batch number/expiry tracking is needed
- Freeze first-release scope

## Phase 2: Foundation

- Set up backend package structure
- Configure MySQL connection and base entities
- Create auth skeleton
- Set up frontend routing, layout, and protected routes
- Configure API client and environment variables

## Phase 3: Product and Customer Management

- Build product CRUD
- Build customer CRUD
- Add stock adjustment flow
- Add search and filter UI

## Phase 4: Billing and Ledger

- Build bill creation workflow
- Deduct stock on finalized sale
- Support paid and due sales
- Build customer ledger page
- Build payment collection flow

## Phase 5: Dashboard and Reports

- Daily sales summary
- Due summary
- Low-stock alerts
- Sales report by date
- Dues report

## Phase 6: Quality and Hardening

- Add backend service and controller tests
- Validate edge cases around stock and due calculations
- Improve error handling
- Improve counter-use UX

## Phase 7: Release

- Production config
- Admin seed setup
- Backup guidance
- Smoke testing
- Release notes

## Current Build Order

1. Authentication
2. Product management
3. Customer management
4. Sales/billing
5. Customer payments
6. Dashboard
7. Reports

## Current Sprint Template

### Sprint Goal

- Finish one end-to-end business flow that a real shop can use

### Tasks

- [ ] backend entities and relationships
- [ ] database schema alignment
- [ ] REST endpoints
- [ ] frontend pages and forms
- [ ] integration testing

### Exit Criteria

- feature works end to end
- calculations are correct
- error handling exists
- tested locally
- documented if needed
