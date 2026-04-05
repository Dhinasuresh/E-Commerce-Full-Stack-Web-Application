# QA Checklist

Use this before calling a feature complete.

## Functional Checks

- User can complete the main workflow
- Expected validations are enforced
- Errors are shown clearly
- Empty states are handled
- Loading states are handled

## Backend Checks

- API returns correct status codes
- Invalid input is rejected
- Database writes are correct
- Edge cases are handled
- Logs do not expose secrets

## Frontend Checks

- Navigation works correctly
- Forms keep valid state
- API failures are handled
- Responsive layout works on mobile and desktop
- No broken routes or console errors

## Data Checks

- Required fields are enforced
- Relationships are correct
- Duplicate data is handled correctly
- Delete/update behavior is correct

## Security Checks

- Secrets are not hardcoded
- Sensitive endpoints are protected
- Inputs are validated
- CORS is configured correctly

## Release Checks

- Build succeeds
- Environment variables are documented
- Database migrations or setup steps are documented
- Smoke test completed
