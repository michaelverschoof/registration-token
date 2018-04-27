package nl.michaelv.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@MappedSuperclass
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@NotNull
	@Column(name = "token")
	private String token;

	@NotNull
	@Column(name = "expiration_date")
	private ZonedDateTime expirationDate;

	@NotNull
	@Column(name = "confirmed")
	private boolean confirmed;

	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

	public Token(User user) {
		this.token = generateToken();
		this.user = user;
		this.confirmed = false;
		this.expirationDate = ZonedDateTime.now().plusDays(1);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ZonedDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(ZonedDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public void confirm() {
		this.confirmed = true;
	}

	private String generateToken() {
		return UUID.randomUUID().toString();
	}

}
