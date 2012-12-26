INSERT INTO users 
		(userid,useraccess,username,
			userpass,useremail,userfirstname,
			userlastname,userlastloggedin,
			userdatecreated,userdatemodified)
	VALUES
		(nextval('users_userid_seq'),3,'admin','feadmin','${deploy.admin}',
			'Administrator','',now(),now(),now());
			
INSERT INTO constraints (constraintid,caid) VALUES (nextval('constraints_constraintid_seq'),1);

INSERT INTO requests (requestid,requestconstraint,requesturi)
	VALUES (nextval('requests_requestid_seq'),currval('constraints_constraintid_seq'),'/${deploy.context}/${deploy.template}');