package com.epam.maya_semina.mail.builder;

public class UserBuilder extends BaseBuilder {

	private static final String ����_����_����_������_�� = "����_����_����@������.��";
	private static final String _1627993445 = "1627993445";

	@Override
	public void buildLogin() {
		persona.setUserLogin(����_����_����_������_��);
	}

	@Override
	public void buildPassword() {
		persona.setUserPassword(_1627993445);

	}

}
