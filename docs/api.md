## Bill Service
Before sending a request, make sure request includes a JWT Token 

For RBAC Details, see //Bill endpoints in:
[WebSecurityConfig.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/WebSecurityConfig.java)

Role details: [RoleGroup.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/RoleGroup.java)
See: [BillController.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restcontroller/BillController.java)

### 1. [POST] CREATE BILL

`API-KEY` is needed in header for this request

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
See: [BillRequestDTO.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restdto/request/bill/BillRequestDTO.java)

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

### 2. [PUT] UPDATE BILL

`API-KEY` is needed in header for this request

Send a request `/api/bill/update/{billId}`:
```json
{
    "customerID" : "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
    "serviceName" : "Flight", // Must be one of  "Flight", "Accommodation", "Insurance", "VehicleRental", "TourPackage"
    "serviceReferenceID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d", // The id of order (example: BookingID) 
    "description": "Tiket pulang pergi Jambi-Jakarta",
    "amount" : 2000000
}

```
See: [BillRequestDTO.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restdto/request/bill/BillRequestDTO.java)

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

###  3. [POST] PAY BILL

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

### 4. [GET] ALL BILLS

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
### 5. [GET] ALL BILLS (Services only)

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

### 6. [GET] ALL CUSTOMER'S BILLS

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

### 7. [GET] BILLS DETAILS

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


## Accommodation Service - Property

Before sending a request, make sure request includes a JWT Token 

For RBAC Details, see //Property endpoints in:
[WebSecurityConfig.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/WebSecurityConfig.java)

Role details: [RoleGroup.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/RoleGroup.java)

See: [PropertyController.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restcontroller/PropertyController.java)

### 1. [GET] GET ALL PROPERTIES

Request: `/api/property?name={name}&type={type}&province={province}`

Query parameters (optional):

name: filter by property name

type: filter by property type

province: filter by province

Response:
```json
{
  "status": 200,
  "message": "List of all properties fetched successfully",
  "timestamp": "2025-11-30T15:00:00.000+07:00",
  "data": [
    {
      "propertyID": "8ab98444-12bd-4911-a00b-eef25858d885",
      "propertyName": "Suatu Hotel",
      "type": 1,
      "address": "Jl. Malioboro No. 45, Yogyakarta",
      "province": 3,
      "description": "Hotel bintang 5 dengan fasilitas lengkap dan lokasi strategis di pusat kota.",
      "totalRoom": 8,
      "income": 0,
      "activeStatus": 1
    }
  ]
}
```
### 2. [GET] GET ALL ACTIVE PROPERTIES

Request: `/api/property-active?name={name}&type={type}&province={province}`

Query parameters optional same as above.

Response:
```json
{
  "status": 200,
  "message": "List of all properties fetched successfully",
  "timestamp": "2025-11-30T15:02:00.000+07:00",
  "data": [
    {
      "propertyID": "8ab98444-12bd-4911-a00b-eef25858d885",
      "propertyName": "Suatu Hotel",
      "type": 1,
      "address": "Jl. Malioboro No. 45, Yogyakarta",
      "province": 3,
      "description": "Hotel bintang 5 dengan fasilitas lengkap dan lokasi strategis di pusat kota.",
      "totalRoom": 8,
      "income": 0,
      "activeStatus": 1
    }
  ]
}
```

### 3. [GET] GET PROPERTY DETAIL

Request: `/api/property/{propertyId}?checkIn={checkIn}&checkOut={checkOut}`

Query parameters optional: checkIn, checkOut

Response:

```json
{
  "status": 200,
  "message": "Property fetched successfully",
  "timestamp": "2025-11-30T15:05:00.000+07:00",
  "data": {
    "propertyID": "8ab98444-12bd-4911-a00b-eef25858d885",
    "propertyName": "Suatu Hotel",
    "type": 1,
    "address": "Jl. Malioboro No. 45, Yogyakarta",
    "province": 3,
    "description": "Hotel bintang 5 dengan fasilitas lengkap dan lokasi strategis di pusat kota.",
    "totalRoom": 8,
    "income": 0,
    "activeStatus": 1,
    "listRoomType": [
      {
        "roomTypeID": "e873c480-1686-46ca-9323-066f46455df0",
        "name": "DoubleRoom",
        "price": 500000,
        "description": "Kamar nyaman dengan pemandangan kota dan fasilitas modern.",
        "capacity": 2,
        "facility": "WiFi, AC, TV, Mini Bar, Shower",
        "floor": 2,
        "listRoom": [
          { "roomID": "fdebef85-47e4-4db5-b521-768abcc901dc", "name": "201", "availabilityStatus": 1 }
        ]
      }
    ],
    "ownerName": "Wahono",
    "ownerID": "20f857a3-c529-4ebf-9d59-a94cc3e687bb",
    "createdDate": "2025-11-30T14:34:10.7130389",
    "updatedDate": "2025-11-30T14:34:10.7130389"
  }
}
```

### 4. [POST] CREATE PROPERTY

Request: `/api/property/create`

Request body:
```json
{
  "property": {
    "propertyName": "Suatu Hotel",
    "type": 1,
    "province": 3,
    "ownerId": "20f857a3-c529-4ebf-9d59-a94cc3e687bb",
    "ownerName": "Wahono",
    "address": "Jl. Malioboro No. 45, Yogyakarta",
    "description": "Hotel bintang 5 dengan fasilitas lengkap dan lokasi strategis di pusat kota."
  },
  "roomTypes": [
    {
      "name": "DoubleRoom",
      "facility": "WiFi, AC, TV, Mini Bar, Shower",
      "price": 500000,
      "description": "Kamar nyaman dengan pemandangan kota dan fasilitas modern.",
      "floor": 2,
      "capacity": 2,
      "unit": 5
    }
  ]
}
```

Response:

```json
{
  "status": 201,
  "message": "Property created successfully",
  "timestamp": "2025-11-30T14:34:11.057+07:00",
  "data": { ...property detail as above... }
}
```

### 5. [PUT] UPDATE PROPERTY

Request: `/api/property/update`

Request body:

```json
{
  "property": {
    "propertyId": "HOT-0210-002",
    "propertyName": "Hotel mewah terbaru",
    "address": "Jl. Malioboro No. 50, Yogyakarta",
    "description": "Hotel bintang 5 dengan fasilitas lengkap, lokasi strategis, dan layanan premium."
  },
  "roomTypes": [
    {
      "roomTypeID": "001-Double Room-2",
      "facility": "WiFi, AC, TV, Mini Bar, Shower, Coffee Maker",
      "price": 500000,
      "description": "Kamar nyaman dengan pemandangan kota, fasilitas modern, dan kopi gratis.",
      "capacity": 2
    }
  ]
}
```

Response:

```json
{
  "status": 200,
  "message": "Property updated successfully",
  "timestamp": "2025-11-30T15:10:00.000+07:00",
  "data": { ...updated property detail... }
}
```

### 6. [DELETE] DELETE PROPERTY

Request: `/api/property/delete/{propertyId}`

Response:

```json
{
  "status": 200,
  "message": "Property with ID {propertyId} deleted successfully",
  "timestamp": "2025-11-30T15:12:00.000+07:00",
  "data": null
}
```

### 7. [POST] ADD MAINTENANCE TO PROPERTY

Request: `/api/property/maintenance/add`

Request body:

```json
{
  "roomID": "fdebef85-47e4-4db5-b521-768abcc901dc",
  "startTime": "2025-12-01T09:00:00",
  "endTime": "2025-12-01T17:00:00",
  "description": "Perawatan AC dan cat ulang dinding"
}
```

Response:

```json
{
  "status": 200,
  "message": "Maintenance added to property successfully",
  "timestamp": "2025-11-30T15:15:00.000+07:00",
  "data": null
}
```
### 8. [POST] ADD ROOM TYPES TO PROPERTY

Request: /api/property/add-room-type/{propertyId}

Request body:
```json
{
  "roomTypes": [
    {
      "name": "Suite Room",
      "facility": "WiFi, AC, TV, Mini Bar",
      "price": 1500000,
      "description": "Kamar suite dengan ruang tamu dan balkon",
      "floor": 4,
      "capacity": 3,
      "unit": 2
    }
  ]
}
```json

Response:
```json
{
  "status": 200,
  "message": "Room types added to property successfully",
  "timestamp": "2025-11-30T15:20:00.000+07:00",
  "data": { ...updated property detail... }
}
```

## Accommodation Service - Review 

Before sending a request, make sure request includes a JWT Token 

For RBAC Details, see //Review endpoints in:
[WebSecurityConfig.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/WebSecurityConfig.java)

Role details: [RoleGroup.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/RoleGroup.java)

See: [ReviewController.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restcontroller/ReviewController.java)

### 1. [POST] CREATE REVIEW

Send a request: `/api/review/create`

Request body:
```json
{
    "bookingID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
    "comment": "Hotel bersih dan pelayanan ramah",
    "cleanlinessRating": 5,
    "facilityRating": 4,
    "serviceRating": 5,
    "valueRating": 4
}
```

See: [ReviewRequestDTO.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restdto/request/review/ReviewRequestDTO.java)

Response:

// success
{
    "status": 201,
    "message": "Review created successfully",
    "timestamp": "2025-12-01T10:00:00.000+07:00",
    "data": {
        "reviewID": "1a2b3c4d-5678-90ab-cdef-1234567890ab",
        "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "customerName": "Muhammad Hibrizi",
        "propertyName": "Suatu Hotel",
        "bookingID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "comment": "Hotel bersih dan pelayanan ramah",
        "cleanlinessRating": 5,
        "facilityRating": 4,
        "serviceRating": 5,
        "valueRating": 4,
        "overallRating": 4,
        "createdAt": "2025-12-01T10:00:00.000+07:00"
    }
}

### 2. [GET] GET REVIEW BY ID

Request: `/api/review/{reviewID}`

Response:
```json
{
    "status": 200,
    "message": "Review fetched successfully",
    "timestamp": "2025-12-01T10:05:00.000+07:00",
    "data": {
        "reviewID": "1a2b3c4d-5678-90ab-cdef-1234567890ab",
        "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "customerName": "Muhammad Hibrizi",
        "propertyName": "Suatu Hotel",
        "bookingID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
        "comment": "Hotel bersih dan pelayanan ramah",
        "cleanlinessRating": 5,
        "facilityRating": 4,
        "serviceRating": 5,
        "valueRating": 4,
        "overallRating": 4,
        "createdAt": "2025-12-01T10:00:00.000+07:00"
    }
}
```

### 3. [GET] GET ALL REVIEWS BY PROPERTY

Request: `/api/review/property/{propertyID}`

Response:
```json
{
    "status": 200,
    "message": "Reviews for property fetched successfully",
    "timestamp": "2025-12-01T10:10:00.000+07:00",
    "data": [
        {
            "reviewID": "1a2b3c4d-5678-90ab-cdef-1234567890ab",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "customerName": "Muhammad Hibrizi",
            "propertyName": "Suatu Hotel",
            "bookingID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "comment": "Hotel bersih dan pelayanan ramah",
            "cleanlinessRating": 5,
            "facilityRating": 4,
            "serviceRating": 5,
            "valueRating": 4,
            "overallRating": 4,
            "createdAt": "2025-12-01T10:00:00.000+07:00"
        }
    ]
}
```

### 4. [GET] GET ALL REVIEWS BY CUSTOMER

Request: `/api/review/customer`

Response:
```json
{
    "status": 200,
    "message": "Reviews for customer fetched successfully",
    "timestamp": "2025-12-01T10:15:00.000+07:00",
    "data": [
        {
            "reviewID": "1a2b3c4d-5678-90ab-cdef-1234567890ab",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "customerName": "Muhammad Hibrizi",
            "propertyName": "Suatu Hotel",
            "bookingID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "comment": "Hotel bersih dan pelayanan ramah",
            "cleanlinessRating": 5,
            "facilityRating": 4,
            "serviceRating": 5,
            "valueRating": 4,
            "overallRating": 4,
            "createdAt": "2025-12-01T10:00:00.000+07:00"
        }
    ]
}
```

### 5. [GET] GET ALL REVIEWS BY CUSTOMER ID

Request: `/api/review/customer/{customerID}`

Response:
```json
{
    "status": 200,
    "message": "Reviews for customer fetched successfully",
    "timestamp": "2025-12-01T10:20:00.000+07:00",
    "data": [
        {
            "reviewID": "1a2b3c4d-5678-90ab-cdef-1234567890ab",
            "customerID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "customerName": "Muhammad Hibrizi",
            "propertyName": "Suatu Hotel",
            "bookingID": "0d94db1b-6e90-4a10-bf2d-70113fc2102d",
            "comment": "Hotel bersih dan pelayanan ramah",
            "cleanlinessRating": 5,
            "facilityRating": 4,
            "serviceRating": 5,
            "valueRating": 4,
            "overallRating": 4,
            "createdAt": "2025-12-01T10:00:00.000+07:00"
        }
    ]
}
```


## Accommodation Service - Room and RoomType

Before sending a request, make sure request includes a JWT Token 

For RBAC Details, see //Room and //Room Type endpoints in:
[WebSecurityConfig.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/WebSecurityConfig.java)

Role details: [RoleGroup.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/security/RoleGroup.java)

See: [RoomGlobalController.java](../src/main/java/apap/ti/_5/accommodation_2306165585_be/restcontroller/RoomGlobalController.java)

### Room Type Endpoints

### 1. [GET] GET ALL ROOM TYPES

Request: `/api/room-type`

Response:
```json
{
    "status": 200,
    "message": "List of all room types fetched successfully",
    "timestamp": "2025-12-01T10:00:00.000+07:00",
    "data": [
        {
            "roomTypeID": "uuid",
            "name": "Double Room",
            "price": 500000,
            "description": "Kamar nyaman dengan fasilitas modern",
            "capacity": 2,
            "facility": "WiFi, AC, TV",
            "floor": 2,
            "listRoom": [
                {
                    "roomID": "uuid",
                    "name": "201",
                    "availabilityStatus": 1
                }
            ]
        }
    ]
}
```

### 2. [GET] GET ROOM TYPES BY PROPERTY

Request: `/api/room-type/property/{propertyId}`

Response:
```json
{
    "status": 200,
    "message": "List of all room types with property id {propertyId} fetched successfully",
    "timestamp": "2025-12-01T10:05:00.000+07:00",
    "data": [
        {
            "roomTypeID": "uuid",
            "name": "Double Room",
            "price": 500000,
            "description": "Kamar nyaman dengan fasilitas modern",
            "capacity": 2,
            "facility": "WiFi, AC, TV",
            "floor": 2,
            "listRoom": [
                {
                    "roomID": "uuid",
                    "name": "201",
                    "availabilityStatus": 1
                }
            ]
        }
    ]
}
```

### 3. [GET] GET ROOM TYPE DETAILS

Request: `/api/room-type/{roomTypeId}`

Response:
```json
{
    "status": 200,
    "message": "Room type details id {roomTypeId} fetched successfully",
    "timestamp": "2025-12-01T10:10:00.000+07:00",
    "data": {
        "roomTypeID": "uuid",
        "name": "Double Room",
        "price": 500000,
        "description": "Kamar nyaman dengan fasilitas modern",
        "capacity": 2,
        "facility": "WiFi, AC, TV",
        "floor": 2,
        "listRoom": [
            {
                "roomID": "uuid",
                "name": "201",
                "availabilityStatus": 1
            }
        ]
    }
}
``` 

### 4. [POST] CREATE ROOM TYPE

Request: `/api/room-type/create`

Request body:
```json
{
    "propertyID": "uuid",
    "name": "Double Room",
    "facility": "WiFi, AC, TV",
    "price": 500000,
    "description": "Kamar nyaman dengan fasilitas modern",
    "floor": 2,
    "capacity": 2,
    "unit": 5
}
```

See: AddSingularRoomTypeDTO.java

Response:
```json
{
    "status": 200,
    "message": "Room type created succesfully",
    "timestamp": "2025-12-01T10:15:00.000+07:00",
    "data": {
        "roomTypeID": "uuid",
        "name": "Double Room",
        "price": 500000,
        "description": "Kamar nyaman dengan fasilitas modern",
        "capacity": 2,
        "facility": "WiFi, AC, TV",
        "floor": 2,
        "listRoom": [
            {
                "roomID": "uuid",
                "name": "201",
                "availabilityStatus": 1
            }
        ]
    }
}
```
### Room Endpoints

## 1. [GET] GET ALL ROOMS

Request: `/api/room`

Response:
```json
{
    "status": 200,
    "message": "List of all rooms fetched successfully",
    "timestamp": "2025-12-01T10:20:00.000+07:00",
    "data": [
        {
            "roomID": "uuid",
            "name": "201",
            "availabilityStatus": 1
        },
        {
            "roomID": "uuid",
            "name": "202",
            "availabilityStatus": 0
        }
    ]
}
```

## Accommodation Service - Booking 

Before sending a request, make sure the request includes a JWT Token

For RBAC details, see //Booking endpoints in:
WebSecurityConfig.java

Role details: RoleGroup.java

See: AccommodationBookingController.java

### 1. [GET] GET ALL BOOKINGS

Request: `/api/booking`

Response:
```json
{
    "status": 200,
    "message": "List of all accommodation bookings fetched successfully",
    "timestamp": "2025-12-01T10:00:00.000+07:00",
    "data": [
        {
            "bookingID": "uuid-1234",
            "propertyName": "Suatu Hotel",
            "checkInDate": "2025-12-01T14:00:00",
            "checkOutDate": "2025-12-05T12:00:00",
            "totalPrice": 2000000,
            "status": 1,
            "roomName": "DoubleRoom"
        }
    ]
}
```

See: AllBookingResponseDTO.java

### 2. [GET] GET BOOKING BY ID

Request: `/api/booking/{bookingID}`

Response:
```json
{
    "status": 200,
    "message": "Accommodation booking fetched successfully",
    "timestamp": "2025-12-01T10:05:00.000+07:00",
    "data": {
        "bookingID": "uuid-1234",
        "checkInDate": "2025-12-01T14:00:00",
        "checkOutDate": "2025-12-05T12:00:00",
        "totalDays": 4,
        "totalPrice": 2000000,
        "status": 1,
        "customerID": "uuid-cust",
        "customerName": "John Doe",
        "customerEmail": "john@example.com",
        "customerPhone": "08123456789",
        "breakfast": true,
        "capacity": 2,
        "roomName": "DoubleRoom",
        "roomID": "uuid-room",
        "roomTypeID": "uuid-roomtype",
        "propertyID": "uuid-property",
        "propertyName": "Suatu Hotel",
        "createdDate": "2025-12-01T09:00:00",
        "updatedDate": "2025-12-01T09:00:00"
    }
}
```

See: AccommodationBookingResponseDTO.java

### 3. [POST] CREATE BOOKING

Request: `/api/booking/create`

Request body:
```json
{
    "propertyID": "uuid-property",
    "roomID": "uuid-room",
    "customerID": "uuid-cust",
    "checkInDate": "2025-12-01T14:00:00",
    "checkOutDate": "2025-12-05T12:00:00",
    "isBreakfast": true
}
```

Response:
```json
{
    "status": 201,
    "message": "Accommodation booking created successfully",
    "timestamp": "2025-12-01T10:10:00.000+07:00",
    "data": { ...AccommodationBookingResponseDTO... }
}
```
### 4. [PUT] UPDATE BOOKING

Request: `/api/booking/update/{bookingID}`

Request body:
```json
{
    "propertyID": "uuid-property",
    "roomID": "uuid-room",
    "customerID": "uuid-cust",
    "checkInDate": "2025-12-02T14:00:00",
    "checkOutDate": "2025-12-06T12:00:00",
    "isBreakfast": false
}
```

Response:
```json
{
    "status": 200,
    "message": "Accommodation booking updated successfully",
    "timestamp": "2025-12-01T10:15:00.000+07:00",
    "data": { ...AccommodationBookingResponseDTO... }
}
```
### 5. [PUT] UPDATE BOOKING STATUS

Request: `/api/booking/update/status/{bookingID}`

Response:
```json
{
    "status": 201,
    "message": "Accommodation booking status updated successfully",
    "timestamp": "2025-12-01T10:20:00.000+07:00",
    "data": { ...AccommodationBookingResponseDTO... }
}
```
6. [POST] CANCEL BOOKING

Request: `/api/booking/cancel/{bookingID}`

Response:
```json
{
    "status": 201,
    "message": "Accommodation booking cancelled successfully",
    "timestamp": "2025-12-01T10:25:00.000+07:00",
    "data": { ...AccommodationBookingResponseDTO... }
}
```
### 7. [GET] INCOME STATISTICS

Request: `/api/booking/chart?month={month}&year={year}`

Query parameters (optional):

month: target month

year: target year

Response:
```json
{
    "status": 200,
    "message": "Income statistics for 12/2025 fetched successfully",
    "timestamp": "2025-12-01T10:30:00.000+07:00",
    "data": {
        "propertyStatistics": [
            { "propertyName": "Suatu Hotel", "propertyIncomes": 2000000 },
            { "propertyName": "Hotel Lain", "propertyIncomes": 3500000 }
        ],
        "totalIncome": 5500000
    }
}
```

See: IncomeStatisticsDTO.java