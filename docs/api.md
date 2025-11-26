Before sending a request, make sure request includes a JWT Token 

For RBAC Details, see //Bill endpoints in:
[WebSecurityConfig.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/WebSecurityConfig.java)

Role details: [RoleGroup.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/RoleGroup.java)

## Bill Service

See: [BillController.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restcontroller/BillController.java)

### 1. [POST] CREATE BILL

Send a request `/api/bill/create`:
```json
{
    "customerID" : "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
    "serviceName" : "Flight", // Must be one of  "Flight", "Accommodation", "Insurance", "VehicleRental", "TourPackage"
    "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d", // The id of order (example: BookingID) 
    "description": "Tiket pulang pergi Jambi-Jakarta",
    "amount" : 2000000
}

```
See: [CreateBillRequestDTO.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restdto/request/bill/CreateBillRequestDTO.java)

Response:

```json
// success
{
    "status": 201,
    "message": "Bill created successfully",
    "timestamp": "2025-11-26T14:27:18.250+07:00",
    "data": {
        "billID": "0578b6ff-57e4-448b-952a-919474817639",
        "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "serviceName": "Flight",
        "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "description": "Tiket pulang pergi Jambi-Jakarta",
        "status": 0,
        "amount": 2000000,
        "createdAt": "2025-11-26T14:27:18.1433626",
        "updatedAt": "2025-11-26T14:27:18.1433626",
        "paymentTimestamp": null
    }
}

// insufficient balance
{
    "status": 400,
    "message": "Permintaan tidak valid: Insufficient balance. Please top up balance.",
    "timestamp": "2025-11-26T14:54:50.829+07:00",
    "data": null
}
```
See: [BillResponseDTO.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restdto/response/bill/BillResponseDTO.java)

###  2. [POST] PAY BILL

Send a request `/api/bill/{billId}/pay`:

OPTIONAL PAYLOAD (NOT REQUIRED)
```json
{
    "couponCode" : "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
}
```
See: [BillCouponDTO.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restdto/request/bill/BillCouponDTO.java)

Response:
```json
{
    "status": 200,
    "message": "Bill payed successfully",
    "timestamp": "2025-11-26T14:36:51.605+07:00",
    "data": {
        "billID": "0578b6ff-57e4-448b-952a-919474817639",
        "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "serviceName": "Flight",
        "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "description": "Tiket pulang pergi Jambi-Jakarta",
        "status": 1,
        "amount": 2000000,
        "createdAt": "2025-11-26T14:27:18.143363",
        "updatedAt": "2025-11-26T14:36:51.5695427",
        "paymentTimestamp": "2025-11-26T14:36:51.5627109"
    }
}
```
See: [BillResponseDTO.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restdto/response/bill/BillResponseDTO.java)

### 3. [GET] ALL BILLS

request: `/api/bill`

response:
```json
{
    "status": 200,
    "message": "Get all bills successfully",
    "timestamp": "2025-11-26T15:02:25.491+07:00",
    "data": [
        {
            "billID": "0578b6ff-57e4-448b-952a-919474817639",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "serviceName": "Flight",
            "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "description": "Tiket pulang pergi Jambi-Jakarta",
            "status": 1,
            "amount": 2000000,
            "createdAt": "2025-11-26T14:27:18.143363",
            "updatedAt": "2025-11-26T14:36:51.569543",
            "paymentTimestamp": "2025-11-26T14:36:51.562711"
        },
        {
            "billID": "0b11c540-e742-4862-8091-8853fd4f0c2b",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "serviceName": "Flight",
            "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2121d",
            "description": "Tiket pulang pergi Jambi-Jakarta",
            "status": 0,
            "amount": 2000000000,
            "createdAt": "2025-11-26T14:54:39.437894",
            "updatedAt": "2025-11-26T14:54:39.437894",
            "paymentTimestamp": null
        }
    ]
}
```
### 4. [GET] ALL BILLS (Services only)

request: `/api/bill/{serviceName}`
response:
```json
{
    "status": 200,
    "message": "Fetched flight bills successfully",
    "timestamp": "2025-11-26T14:58:27.004+07:00",
    "data": [
        {
            "billID": "0578b6ff-57e4-448b-952a-919474817639",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "serviceName": "Flight",
            "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "description": "Tiket pulang pergi Jambi-Jakarta",
            "status": 1,
            "amount": 2000000,
            "createdAt": "2025-11-26T14:27:18.143363",
            "updatedAt": "2025-11-26T14:36:51.569543",
            "paymentTimestamp": "2025-11-26T14:36:51.562711"
        },
        {
            "billID": "0b11c540-e742-4862-8091-8853fd4f0c2b",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "serviceName": "Flight",
            "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2121d",
            "description": "Tiket pulang pergi Jambi-Jakarta",
            "status": 0,
            "amount": 2000000000,
            "createdAt": "2025-11-26T14:54:39.437894",
            "updatedAt": "2025-11-26T14:54:39.437894",
            "paymentTimestamp": null
        }
    ]
}
```

### 5. [GET] ALL CUSTOMER'S BILLS

request: `/api/bill/customer`

response:
```json
{
    "status": 200,
    "message": "Fetched all customer's bills successfully",
    "timestamp": "2025-11-26T15:05:04.082+07:00",
    "data": [
        {
            "billID": "0578b6ff-57e4-448b-952a-919474817639",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "serviceName": "Flight",
            "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "description": "Tiket pulang pergi Jambi-Jakarta",
            "status": 1,
            "amount": 2000000,
            "createdAt": "2025-11-26T14:27:18.143363",
            "updatedAt": "2025-11-26T14:36:51.569543",
            "paymentTimestamp": "2025-11-26T14:36:51.562711"
        },
        {
            "billID": "0b11c540-e742-4862-8091-8853fd4f0c2b",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "serviceName": "Flight",
            "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2121d",
            "description": "Tiket pulang pergi Jambi-Jakarta",
            "status": 0,
            "amount": 2000000000,
            "createdAt": "2025-11-26T14:54:39.437894",
            "updatedAt": "2025-11-26T14:54:39.437894",
            "paymentTimestamp": null
        }
    ]
}
```

### 6. [GET] BILLS DETAILS

request: `api/bill/detail/{billId}`
response:
```json
{
    "status": 200,
    "message": "Fetched bill details successfully",
    "timestamp": "2025-11-26T15:38:38.646+07:00",
    "data": {
        "billID": "0578b6ff-57e4-448b-952a-919474817639",
        "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "serviceName": "Flight",
        "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "description": "Tiket pulang pergi Jambi-Jakarta",
        "status": 1,
        "amount": 2000000,
        "createdAt": "2025-11-26T14:27:18.143363",
        "updatedAt": "2025-11-26T14:36:51.569543",
        "paymentTimestamp": "2025-11-26T14:36:51.562711"
    }
}
```


