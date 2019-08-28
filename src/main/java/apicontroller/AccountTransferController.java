package apicontroller;

import data.BankAccountDAO;
import exception.InSufficentFundExcpetion;
import io.javalin.Context;
import io.javalin.Handler;
import model.Account;
import model.Currency;
import model.Transfer;
import utility.AmountUtil;

public class AccountTransferController {
    public static Handler createTransfer = ctx->{
        Transfer newTransfer;
        try {
            newTransfer = ctx.bodyAsClass(Transfer.class);
        }catch (Exception e){
            System.out.println("Create Transfer Error --> invalid payload " + ctx.body());
            ctx.status(HTTPCodes.BAD_REQUEST.getCode());
            ctx.result(ApiResult.INVALID_REQUEST.toJSON());
            return;
        }

        final double TRANSFER_AMOUNT = newTransfer.getAmount();
        // check if transfer amount is greater than 0
        if(TRANSFER_AMOUNT <= 0 || AmountUtil.getFloatDigitCount(TRANSFER_AMOUNT) > 2){
            ctx.status(HTTPCodes.BAD_REQUEST.getCode());
            ctx.result(ApiResult.INVALID_AMOUNT.toJSON());
            return;
        }

        // check if accounts are exists
        Account sourceAccount = BankAccountDAO.getInstance().getAccount(newTransfer.getSourceAccountID());
        Account destinationAccount = BankAccountDAO.getInstance().getAccount(newTransfer.getDestinationAccountID());
        if(sourceAccount == null || destinationAccount == null){
            ctx.status(HTTPCodes.BAD_REQUEST.getCode());
            ctx.result(ApiResult.ACCOUNT_NOT_FOUND.toJSON());
            return;
        }

        if(sourceAccount.getAccountID().equals(destinationAccount.getAccountID())){
            ctx.status(HTTPCodes.BAD_REQUEST.getCode());
            ctx.result(ApiResult.TRANSFER_EQUAL_ACCOUNTIDS.toJSON());
            return;
        }

        Currency transferCurrency = newTransfer.getCurrency();
        // check if currency is valid
        if(transferCurrency == null){
            ctx.status(HTTPCodes.BAD_REQUEST.getCode());
            ctx.result(ApiResult.INVALID_CURRENCY.toJSON());
            return;
        }

        // check if currency is equal for both accounts
        if(sourceAccount.getCurrency().getType() != transferCurrency.getType() ||
                destinationAccount.getCurrency().getType() != transferCurrency.getType()){
            ctx.status(HTTPCodes.BAD_REQUEST.getCode());
            ctx.result(ApiResult.CURRENCY_DOES_NOT_MATCH.toJSON());
            return;
        }

        // check if source account has enough balance
        if(sourceAccount.getBalance() < TRANSFER_AMOUNT){
            ctx.status(HTTPCodes.FORBIDDEN.getCode());
            ctx.result(ApiResult.INSUFFICIENT_FUNDS.toJSON());
            return;
        }

        try{
            transferFund(sourceAccount,destinationAccount,TRANSFER_AMOUNT,ctx);
        }catch (InSufficentFundExcpetion ex){
            ctx.status(HTTPCodes.FORBIDDEN.getCode());
            ctx.result(ApiResult.INSUFFICIENT_FUNDS.toJSON());
            return;
        }catch(Exception ex){
            ctx.status(HTTPCodes.INTERNAL_SERVER_ERROR.getCode());
            ctx.result(ApiResult.SYSTEM_ERROR.toJSON());
            return;
        }

        ctx.status(HTTPCodes.SUCCESS.getCode());
        ctx.result(ApiResult.SUCCESS.toJSON());
    };

    public static void transferFund(final Account source, final Account destination, double amount, Context ctx) throws InSufficentFundExcpetion {
        String sourceLock, destLock;
        sourceLock = Integer.valueOf(source.getAccountID()) < Integer.valueOf(destination.getAccountID()) ? source.getAccountID() : destination.getAccountID();
        destLock = Integer.valueOf(source.getAccountID()) < Integer.valueOf(destination.getAccountID()) ? destination.getAccountID() :source.getAccountID();
        synchronized (sourceLock) {
            if(source.getBalance() < amount){
                throw new InSufficentFundExcpetion("The source account does not have enough blance");
            }
            synchronized (destLock) {
                System.out.println("Source account lock obtained" + sourceLock);
                source.withdraw(amount);
                System.out.println("Destination account lock obtained" + destLock);
                destination.deposit(amount);
            }
        }
    }
}
