package apap.ti._5.accommodation_2306165585_be.security;

public class RoleGroup {

    public static final String SUPERADMIN = "SUPERADMIN";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String ACCOMMODATION_OWNER = "ACCOMMODATION_OWNER";
    public static final String FLIGHT_AIRLINE = "FLIGHT_AIRLINE";
    public static final String RENTAL_VENDOR = "RENTAL_VENDOR";
    public static final String INSURANCE_PROVIDER = "INSURANCE_PROVIDER";
    public static final String TOUR_PACKAGE_VENDOR = "TOUR_PACKAGE_VENDOR";

    public static final String[] ALL_ROLES = {
        SUPERADMIN,
        CUSTOMER,
        ACCOMMODATION_OWNER,
        FLIGHT_AIRLINE,
        RENTAL_VENDOR,
        INSURANCE_PROVIDER,
        TOUR_PACKAGE_VENDOR
    };

    public static final String[] OWNER_ROLES = {
        SUPERADMIN,
        ACCOMMODATION_OWNER,
        FLIGHT_AIRLINE,
        RENTAL_VENDOR,
        INSURANCE_PROVIDER,
        TOUR_PACKAGE_VENDOR
    };

    public static final String[] SERVICE_ROLES = {
        ACCOMMODATION_OWNER,
        FLIGHT_AIRLINE,
        RENTAL_VENDOR,
        INSURANCE_PROVIDER,
        TOUR_PACKAGE_VENDOR
    };
    
    public static final String[] PROPERTY_ROLES = {
        SUPERADMIN,
        ACCOMMODATION_OWNER,
        CUSTOMER,
    };

    public static final String[] PROPERTY_OWNER_ONLY = {
        SUPERADMIN,
        ACCOMMODATION_OWNER
    };

    public static final String[] BILL_VIEW_ROLES = {
        SUPERADMIN,
        ACCOMMODATION_OWNER,
        CUSTOMER
    };

    public static final String[] BILL_CREATE_ROLES = {
        SUPERADMIN,
        ACCOMMODATION_OWNER
    };

}

