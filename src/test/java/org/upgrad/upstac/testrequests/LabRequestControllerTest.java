package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.Contracts;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.UserService;
import org.upgrad.upstac.users.models.Gender;
import org.upgrad.upstac.users.roles.UserRole;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@Slf4j
class LabRequestControllerTest {


    @Autowired
    LabRequestController labRequestController;

    @Autowired
    TestRequestQueryService testRequestQueryService;

    @Mock
    UserLoggedInService userLoggedInService;

    @Autowired
    private UserService userService;


    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status() {

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.INITIATED);
        //Implement this method
        User user = getUser();

        when(userLoggedInService.getLoggedInUser()).thenReturn(user);

        //Create another object of the TestRequest method and explicitly assign this object for Lab Test using assignForLabTest() method
        // from labRequestController class. Pass the request id of testRequest object.

        TestRequest result = labRequestController.assignForLabTest(testRequest.getRequestId());

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'INITIATED'
        // make use of assertNotNull() method to make sure that the lab result of second object is not null
        // use getLabResult() method to get the lab result

        assertEquals(result.getRequestId(), testRequest.getRequestId());
        Contracts.assertNotNull(result.getLabResult());
        assertEquals(result.getStatus(), RequestStatus.LAB_TEST_IN_PROGRESS);
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    private TestRequest prepareMockTestRequest() {
        TestRequest testRequest = new TestRequest();
        testRequest.setAddress("Some address");
        testRequest.setAge(39);
        testRequest.setCreated(LocalDate.now());
        testRequest.setCreatedBy(getUser());
        testRequest.setEmail("ravi@ravi.com");
        testRequest.setGender(Gender.MALE);
        testRequest.setName("Ravi");
        testRequest.setPhoneNumber("456465456");
        testRequest.setPinCode(4545);
        testRequest.setRequestId(1L);
        return testRequest;
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception() {

        Long InvalidRequestId = -34L;

        //Implement this method
        User user = getUser();

        when(userLoggedInService.getLoggedInUser()).thenReturn(user);

        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForLabTest() method
        // of labRequestController with InvalidRequestId as Id

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.assignForLabTest(InvalidRequestId);
        });

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertThat("Invalid ID", containsString(result.getReason()));
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details() {

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

        //Implement this method
        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        User user = getUser();

        when(userLoggedInService.getLoggedInUser()).thenReturn(user);

        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'LAB_TEST_IN_PROGRESS'. Make use of updateLabTest() method from labRequestController class (Pass the previously created two objects as parameters)
        CreateLabResult createLabResult = new CreateLabResult();
        createLabResult.setTemperature("108");
        createLabResult.setOxygenLevel("92");
        createLabResult.setHeartBeat("108");
        createLabResult.setBloodPressure("111");
        createLabResult.setComments("not ok");
        createLabResult.setResult(TestStatus.POSITIVE);
        TestRequest result = labRequestController.updateLabTest(testRequest.getRequestId(), createLabResult);
        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_COMPLETED'
        // 3. the results of both the objects created should be same. Make use of getLabResult() method to get the results.
        assertThat(result.getRequestId().toString(), containsString(testRequest.getRequestId().toString()));
        assertThat(result.getStatus().toString(), containsString(RequestStatus.LAB_TEST_COMPLETED.toString()));
        assertThat(result.getStatus().toString(), containsString(createLabResult.getResult().toString()));
    }


    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception() {

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);


        //Implement this method
        User user = getUser();

        when(userLoggedInService.getLoggedInUser()).thenReturn(user);

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.assignForLabTest(testRequest.getRequestId());
        });

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertThat("Invalid ID", containsString(result.getReason()));

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception() {

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

        //Implement this method
        User user = getUser();

        when(userLoggedInService.getLoggedInUser()).thenReturn(user);

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        // Set the result of the above created object to null.

        CreateLabResult createLabResult = getCreateLabResult(testRequest);
        createLabResult.setResult(null);
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.updateLabTest(testRequest.getRequestId(), createLabResult);
        });

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "ConstraintViolationException"
        assertThat(result.getReason(), containsString("ConstraintViolationException"));
    }

    public CreateLabResult getCreateLabResult(TestRequest testRequest) {

        //Create an object of CreateLabResult and set all the values
        // Return the object
        CreateLabResult createLabResult = new CreateLabResult();
        createLabResult.setComments("comments");
        createLabResult.setBloodPressure("111");
        createLabResult.setHeartBeat("43");
        createLabResult.setOxygenLevel("22");
        createLabResult.setTemperature("102");
        createLabResult.setResult(testRequest.getLabResult().getResult());
        return createLabResult;
    }


    private User getUser() {
        User user = new User();
        user.setId(7L);
        user.setUserName("ravitest");
        user.setRoles(userService.getRoleFor(UserRole.TESTER));
        return user;
    }
}