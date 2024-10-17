import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User (
    @Id
    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String
) {
}