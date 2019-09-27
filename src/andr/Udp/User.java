package andr.Udp;

import andr.Main;

import java.io.Serializable;

public class User implements Serializable {

    private String login;
    private String password;
    private String email;

    public User(String login, String password, String email){
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEncodedPassword(){
        StringHasher hasher = new StringHasher("MD2");
        return hasher.getHash(this.password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "ПОльзователь " + login;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (!login.equals(other.login))
            return false;
        if (!password.equals(other.password))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 5;
        int result = 18;
        result = result * prime + Main.strHashCode(login) * (int) Math.pow(prime,2);
        result = result * prime + Main.strHashCode(password) * (int) Math.pow(prime,3);
        if(email != null)
            result = result * prime + Main.strHashCode(email) * (int) Math.pow(prime,4);
        return result;
    }

}
