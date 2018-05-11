package nl.michaelv.model.tokens;

import lombok.Getter;
import nl.michaelv.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class AbstractToken implements Token {

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

	public AbstractToken(User user) {
		this.token = generateToken();
		this.user = user;
		this.confirmed = false;
		this.expirationDate = ZonedDateTime.now().plusDays(EXPIRATION_DAYS);
	}

	public AbstractToken(String token, User user) {
		this.token = token;
		this.user = user;
		this.confirmed = false;
		this.expirationDate = ZonedDateTime.now().plusDays(EXPIRATION_DAYS);
	}

	public String token() {
		return this.token;
	}

	public User user() {
		return this.user;
	}

	public void confirm() {
		this.confirmed = true;
	}

	public boolean confirmed() {
		return this.confirmed;
	}

	public boolean expired() {
		return this.getExpirationDate().isBefore(ZonedDateTime.now());
	}

	private String generateToken() {
		return UUID.randomUUID().toString();
	}

}
