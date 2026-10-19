import axios from 'axios';
import { environment } from  '../../../environments/environment';


async function deposit(amount: number, currency: string){

    const response = await axios.post(`${environment.apiUrl}/payment/deposit`, {
        amount: amount,
        currency: currency
    });

    return response.data;
}

async function withdraw(recipientEmail: string, amount : number, currency: string, userId: number){
        const response = await axios.post(`${environment.apiUrl}/payment/withdraw`, {
            recipientEmail: recipientEmail,
            amount: amount,
            currency: currency,
            userId: userId
        });

        return response.data;
}

export const PaymentService = {
    deposit,
    withdraw
}
