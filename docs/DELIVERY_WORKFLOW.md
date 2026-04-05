# Delivery Workflow

This is the working process we will follow for each feature.

## 1. Define

- Write the feature in `docs/PRODUCT_REQUIREMENTS.md`
- Confirm business rules
- Confirm what is not included

## 2. Design

- Update `docs/ARCHITECTURE.md`
- Decide entities, endpoints, and UI flow
- Identify risks before coding

## 3. Build

- Implement backend first when API/domain is primary
- Implement frontend first when validating UX is primary
- Keep changes small and testable

## 4. Verify

- Run backend checks
- Run frontend checks
- Test the full user flow
- Use `docs/QA_CHECKLIST.md`

## 5. Release

- Prepare production config
- Run smoke tests
- Document known limitations

## 6. Improve

- Collect bugs
- Prioritize enhancements
- Update roadmap for next iteration

## Working Rule

Do not start coding until these are clear:

- what problem the feature solves
- who the user is
- what success looks like
- what data is needed
- what the API contract is
