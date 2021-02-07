package tr.k12.enka.networking;

public class AuthHandler {
    static void authenticate(String mail, String token) {
        DataStore.setMail(mail);
        DataStore.setToken(token);
    }

    static void quit() {
        DataStore.setToken(null);
        DataStore.setMail(null);
    }
}
