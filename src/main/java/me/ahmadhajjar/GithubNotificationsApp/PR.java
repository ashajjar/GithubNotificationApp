package me.ahmadhajjar.GithubNotificationsApp;


import java.util.Objects;

public class PR {
    private String username;
    private Integer prId;

    public PR() {
    }

    public PR(String username, Integer prId) {
        this.username = username;
        this.prId = prId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPrId() {
        return prId;
    }

    public void setPrId(Integer prId) {
        this.prId = prId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PR pr = (PR) o;
        return Objects.equals(username, pr.username) && Objects.equals(prId, pr.prId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(username);
        result = 31 * result + Objects.hashCode(prId);
        return result;
    }

    @Override
    public String toString() {
        return this.prId + " - " + this.username;
    }
}
