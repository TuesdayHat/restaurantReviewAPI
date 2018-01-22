package models;

import com.sun.org.apache.regexp.internal.RE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Guest on 1/22/18.
 */
public class RestaurantTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getNameReturnsCorrectName() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals("Slammin' Nutt", testRestaurant.getName());
    }

    @Test public void getAddressReturnsCorrectAddress() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals("200 Silversmith Ave", testRestaurant.getAddress());
    }

    @Test public void getZipcodeReturnsCorrectZipcode() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals("97100", testRestaurant.getZipcode());
    }

    @Test public void getPhoneReturnsCorrectPhone() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals("503-402-9874", testRestaurant.getPhone());
    }

    @Test public void getWebsiteReturnsCorrectWebsite() throws Exception {
        Restaurant testRestaurant = setupAltRestaurant();
        assertEquals("no website listed", testRestaurant.getWebsite());
    }

    @Test public void getEmailReturnsCorrectEmail() throws Exception {
        Restaurant testRestaurant = setupAltRestaurant();
        assertEquals("no email given", testRestaurant.getEmail());
    }

    @Test
    public void setNameSetsCorrectName() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        testRestaurant.setName("Mindrite Coffee");
        assertNotEquals("Slammin' Nutt", testRestaurant.getName());
    }

    @Test
    public void setAddressSetsCorrectAddress() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        testRestaurant.setAddress("60 Beach Way");
        assertNotEquals("200 Silversmith Avenue", testRestaurant.getAddress());
    }

    @Test
    public void setZipcodeSetsCorrectZipcode() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        testRestaurant.setZipcode("97000");
        assertNotEquals("97100", testRestaurant.getZipcode());
    }

    @Test
    public void setPhoneSetsCorrectPhone() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        testRestaurant.setPhone("310-555-JEAN");
        assertNotEquals("503-402-9874", testRestaurant.getName());
    }

    @Test
    public void setWebsiteSetsCorrectWebsite() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        testRestaurant.setWebsite("http://www.jeantime.com");
        assertNotEquals("http://www.donutwolf.com", testRestaurant.getWebsite());
    }

    @Test
    public void setEmailSetsCorrectEmail() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        testRestaurant.setName("jean@jeantime.com");
        assertNotEquals("isaac@slamminnutt.net", testRestaurant.getName());
    }

    public Restaurant setupRestaurant(){
        return new Restaurant("Slammin' Nutt", "200 Silversmith Ave", "97100", "503-402-9874", "http://www.donutwolf.com", "isaac@slamminnutt.net");
    }

    public Restaurant setupAltRestaurant() {
        return new Restaurant("Slammin' Nutt", "200 Silversmith Ave", "97100", "503-401-9874");
    }
}