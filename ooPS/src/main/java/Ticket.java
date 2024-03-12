public class Ticket {
    private Integer ticketID;
    private Integer numAccompanyingGuests;

    public Ticket(Integer ticketID, Integer numAccompanyingGuests) {
        this.ticketID = ticketID;
        this.numAccompanyingGuests = numAccompanyingGuests;
    }

    public Integer getTicketID() {
        return this.ticketID;
    }

    public Integer getNumAccompanyingGuests() {
        return this.numAccompanyingGuests;
    }

    public int setNumAccompanyingGuests(Integer guests) {
        this.numAccompanyingGuests = guests;
        // if database is successfully updated, return 0
        // if failed, return -1
        return 0;
    }
}