package nl.michaelv.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
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

	private static final long EXPIRATION_DAYS = 1;

	public Token(User user) {
		this.token = generateToken();
		this.user = user;
		this.confirmed = false;
		this.expirationDate = ZonedDateTime.now().plusDays(EXPIRATION_DAYS);
	}

	public void confirm() {
		this.confirmed = true;
	}

	private String generateToken() {
		return UUID.randomUUID().toString();
	}

}
