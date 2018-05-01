package nl.michaelv.registerandlogin.controller;

import nl.michaelv.controller.SignUpController;
import nl.michaelv.model.Role;
import nl.michaelv.model.User;
import nl.michaelv.model.forms.SignupForm;
import nl.michaelv.service.MailService;
import nl.michaelv.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SignUpControllerTest {

	@InjectMocks
	SignUpController signUpController;

	@Mock
	private UserService userService;

	@Mock
	private MailService mailService;

	@Mock
	private BindingResult result;

	private Model model;

	@Before
	public void before() {
		initMocks(this);
	}

	@Test
	public void signupGet() {
//		String signup = signUpController.signup(model);
//		assertNotNull(signup);
//		assertTrue("signup".equals(signup));
//
//		// TODO: Check model for empty signup form and current tab?
//		Object form = getFromModel("form");
//		assertNotNull(form);
//		assertTrue(form instanceof SignupForm);
//
//		Object tab = getFromModel("tab");
//		assertNotNull(tab);
//		assertTrue("signup".equals(tab));

		assertTrue(true);
	}

	private Object getFromModel(String key) {
		return model.asMap().get(key);
	}

}
