**Secure Network Messenger**
------------------------

This project is part of curriculum of UTDallas : Network Security CS6349


**Overview** 

This project exhibits custom SSl layer using various libraries and protocol techniques. It basically start and maintain secure end to end connection between two chat clients and server.

**Protocol Design** 

(1) Authentication with server 

![](/SupportedImages/authentication.jpg?raw=true)

(2) Customize Kerberos for TGT of another client

![](/SupportedImages/kerberos.jpg?raw=true)

(3) Message Transfer from one client to another client 

![](/SupportedImages/msg.jpg?raw=true)



**Security Features**

1.	Authentication: Client is authenticated using password based authentication. When client enters the username and password into the workstation, the workstation generates SHA512 hash of the password.
2.	Communication: Communication between clients is session based. That is, for each session a session key is assigned by the server for client to client or peer to peer communication. This session key remains valid only for that particular session.
3.	Confidentiality: As the session keys are known only to the clients, the message to be sent remains confidential.
4.	Integrity: Integrity is maintained by sending HMAC of the message along with the message. If the message is changed by the attacker, its integrity won’t be the same when calculated at the receiver end. Hence, a change in message won’t go undetected.
5.	Attacks considered
1.      Eavesdropping
2.      Man in the Middle
3.      Replay attack
4.      Message modification(Integrity)
5.      Active attacker posing as Alice to server or other clients
