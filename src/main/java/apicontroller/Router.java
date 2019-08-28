package apicontroller;

import io.javalin.Javalin;

public class Router {
    private Javalin router;
    public Router(Javalin router){
        this.router = router;
        this.setRoutes();
    }
    private void setRoutes(){
        /* Account */
        this.router.post("/account", BankAccountController.createAccount);
        this.router.get("/account/:id", BankAccountController.getAccount);
        this.router.delete("/account/:id", BankAccountController.deleteAccount);
        /* Transfer */
        this.router.post("/transfer", AccountTransferController.createTransfer);
    }
}
