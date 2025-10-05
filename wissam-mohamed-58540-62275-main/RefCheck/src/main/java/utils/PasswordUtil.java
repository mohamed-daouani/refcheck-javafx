    package utils;

    import org.mindrot.jbcrypt.BCrypt;

    public class PasswordUtil {

        /**
         * Hashes a plain text password using BCrypt with a salt factor of 12.
         *
         * @param plainTextPassword the plain text password to hash
         * @return the hashed password as a String
         */
        public static String hashPassword(String plainTextPassword) {

            return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
        }

        /**
         * Checks whether a plain text password matches a given BCrypt hashed password.
         *
         * @param plainTextPassword the plain text password to verify
         * @param hashedPassword    the previously hashed password
         * @return true if the password matches, false otherwise
         */
        public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
            if (plainTextPassword == null || hashedPassword == null || plainTextPassword.isEmpty() || hashedPassword.isEmpty()) {
                throw new IllegalArgumentException("Password inputs cannot be null or empty");
            }
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        }
    }
