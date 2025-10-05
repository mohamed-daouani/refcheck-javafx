package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testHashPasswordGeneratesDifferentHashes() {
        String password = "MySecret123";

        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2, "Two hashes of the same password should be different due to salting");
    }

    @Test
    void testCheckPasswordWithCorrectPassword() {
        String password = "MySecret123";
        String hash = PasswordUtil.hashPassword(password);

        assertTrue(PasswordUtil.checkPassword(password, hash), "The correct password should match the hash");
    }

    @Test
    void testCheckPasswordWithIncorrectPassword() {
        String password = "MySecret123";
        String wrongPassword = "WrongSecret456";
        String hash = PasswordUtil.hashPassword(password);

        assertFalse(PasswordUtil.checkPassword(wrongPassword, hash), "An incorrect password should not match the hash");
    }

    @Test
    void testCheckPasswordWithNullInputs() {
        String password = "MySecret123";
        String hash = PasswordUtil.hashPassword(password);

        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.checkPassword(null, hash));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.checkPassword(password, null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.checkPassword(null, null));
    }

    @Test
    void testCheckPasswordWithEmptyInputs() {
        String password = "MySecret123";
        String hash = PasswordUtil.hashPassword(password);

        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.checkPassword("", hash));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.checkPassword(password, ""));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.checkPassword("", ""));
    }

    @Test
    void testHashPasswordWithEmptyString() {
        String emptyPassword = "";
        String hash = PasswordUtil.hashPassword(emptyPassword);

        assertNotNull(hash, "Even an empty password should return a hash");

        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.checkPassword(emptyPassword, hash));
    }

}
