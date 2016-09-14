package com.epam.maya_semina.mail.builder;

public class UserBuilder extends BaseBuilder {

	private static final String ТЕСТ_МАИЛ_БОКС_ПИСЬМО_РФ = "тест_маил_бокс@Письмо.РФ";
	private static final String _1627993445 = "1627993445";

	@Override
	public void buildLogin() {
		persona.setUserLogin(ТЕСТ_МАИЛ_БОКС_ПИСЬМО_РФ);
	}

	@Override
	public void buildPassword() {
		persona.setUserPassword(_1627993445);

	}

}
