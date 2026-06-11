# Countries API Documentation

Version: 1.0
Last updated: 2026-06-11

Overview
This document describes the REST endpoints exposed by the Countries API service. The service synchronizes country data from a third‑party SOAP service into a local database and serves all client queries from that database.

Base URL
- Local development: http://localhost:8080/api/v1
- Kubernetes (example Service): http://countries-svc.ncba-countries.svc.cluster.local/api/v1
- Ingress (if configured): http(s)://<your-ingress-host>/api/v1

Authentication
- None (public endpoints for the scope of this exercise).

Common Response Wrapper
All endpoints return a unified JSON envelope:
{
  "statusCode": "0" | "1",         // 0 = Success, 1 = Failed
  "statusMessage": "Success" | "Failed",
  "data": <payload or null>
}
- statusCode and statusMessage are mapped from the TransactionStatus enum.
- On success, data contains the requested payload; on failure, data is null.

Pagination and Sorting
List endpoints support pagination and sorting.
- Query parameters:
  - page: integer, 0-based (default 0)
  - size: integer, page size (default 10)
  - sortBy: property name to sort by (default name). Supported: name, capital, currency, continent, language
  - sortDir: asc or desc (default asc)
- Response shape for paged endpoints (inside data):
{
  "content": [ /* array of CountryResponse */ ],
  "pageable": { /* paging details */ },
  "totalElements": 250,
  "totalPages": 25,
  "size": 10,
  "number": 0,
  "sort": { /* sort details */ },
  "first": true,
  "last": false,
  "numberOfElements": 10,
  "empty": false
}
Note: This is the standard Spring Data Page serialization.

Data Model (DTOs)
CountryResponse
{
  "isoCode": "KE",
  "name": "Kenya",
  "capital": "Nairobi",
  "currency": "KES",
  "continent": "AF",      // continent code
  "language": "English, Swahili"  // comma-separated languages
}

Request Parameters
For each endpoint, the following request parameters are supported.

1) GET /countries
- Request scope: Query parameters

| Name     | Type    | Required | Description |
|----------|---------|----------|-------------|
| name     | string  | No       | Substring match (case-insensitive) on the country name. |
| continent| string  | No       | Exact match (case-insensitive) on continent code (e.g., AF, EU, AS, NA, SA, OC). |
| currency | string  | No       | Exact match (case-insensitive) on currency ISO code (e.g., USD, EUR, KES). |
| language | string  | No       | Substring match (case-insensitive) against the stored comma-separated languages. |
| page     | integer | No       | Page number, 0-based. Default: 0. |
| size     | integer | No       | Page size. Default: 10. |
| sortBy   | string  | No       | Property to sort by. Supported: name, capital, currency, continent, language. Default: name. |
| sortDir  | string  | No       | Sort direction: asc or desc. Default: asc. |

2) GET /countries/{isoCode}
- Request scope: Path parameter

| Name    | Type   | Required | Description |
|---------|--------|----------|-------------|
| isoCode | string | Yes      | Country ISO code (e.g., KE, US, GB). Case-insensitive. |

3) GET /countries/by-currency/{currency}
- Request scope: Path + Query parameters

| Name    | Type    | Required | Description |
|---------|---------|----------|-------------|
| currency| string  | Yes      | Currency ISO code in the path (e.g., USD, EUR, KES). |
| page    | integer | No       | Page number, 0-based. Default: 0. |
| size    | integer | No       | Page size. Default: 10. |
| sortBy  | string  | No       | Property to sort by. Supported: name, capital, currency, continent, language. Default: name. |
| sortDir | string  | No       | Sort direction: asc or desc. Default: asc. |

Endpoints
1) Search/Filter Countries (Paginated)
GET /countries
- Description: Returns countries filtered by optional criteria. If the database is empty, the service performs an initial sync from the third‑party SOAP service.
- Query parameters (all optional unless stated otherwise):
  - name: substring match (case-insensitive) on the country name
  - continent: exact match (case-insensitive) on continent code (e.g., AF, EU, AS, NA, SA, OC)
  - currency: exact match (case-insensitive) on currency ISO code (e.g., USD, EUR, KES)
  - language: substring match (case-insensitive) against the stored comma-separated languages
  - page, size, sortBy, sortDir: see Pagination and Sorting
- Responses:
  - 200 OK (wrapped): statusCode="0", data=Page<CountryResponse>
  - 200 OK (wrapped failure): statusCode="1", data=null (if an internal error occurs)
- cURL example:
  curl -G "http://localhost:8080/api/v1/countries" \
    --data-urlencode "name=ken" \
    --data-urlencode "page=0" \
    --data-urlencode "size=10" \
    --data-urlencode "sortBy=name" \
    --data-urlencode "sortDir=asc"

2) Get Country Details by ISO Code
GET /countries/{isoCode}
- Description: Retrieves a single country by its ISO code (case-insensitive). If the database is empty, the service attempts an initial sync.
- Path parameters:
  - isoCode: string (e.g., KE, US, GB)
- Responses:
  - 200 OK (wrapped): statusCode="0", data=CountryResponse (when found)
  - 200 OK (wrapped failure): statusCode="1", data=null (if not found or on error)
- cURL example:
  curl "http://localhost:8080/api/v1/countries/KE"

3) Countries Sharing the Same Currency (Paginated)
GET /countries/by-currency/{currency}
- Description: Returns a paginated list of countries using the specified currency code.
- Path parameters:
  - currency: string (e.g., USD, EUR, KES)
- Query parameters:
  - page, size, sortBy, sortDir: see Pagination and Sorting
- Responses:
  - 200 OK (wrapped): statusCode="0", data=Page<CountryResponse>
  - 200 OK (wrapped failure): statusCode="1", data=null (on error)
- cURL example:
  curl -G "http://localhost:8080/api/v1/countries/by-currency/USD" \
    --data-urlencode "page=0" \
    --data-urlencode "size=10" \
    --data-urlencode "sortBy=name" \
    --data-urlencode "sortDir=asc"

Notes on Data Freshness
- On first access, if the database has no countries, the service will call the upstream SOAP endpoint (http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso) and persist all returned countries, including a comma-separated list of languages. Afterwards, all queries are served from the database.

Error Handling
- This sample returns HTTP 200 with a wrapped failure (statusCode="1", statusMessage="Failed", data=null) on errors or not-found cases. In production, you may prefer to use proper HTTP error codes (4xx/5xx) and richer error payloads.

Postman Collection
- A ready-to-import Postman collection is included: postman/backend-engineer-test.postman_collection.json
- It contains example requests for all endpoints with variables for base URL, pagination, and sorting.

Versioning
- The current API is unversioned beyond the base path /api/v1. Breaking changes should be introduced under a new versioned path.

Contact
- For questions or issues, please open a ticket in the repository or contact the maintainers.
