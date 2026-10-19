import axios from 'axios';
import { environment }  from '../../../environments/environment'

async function getUsers() {
    const response = await axios.get(`${environment.apiUrl}/users/1`)
    return response.data;
}

async function getUserById(id: number){
    const response = await axios.get(`${environment.apiUrl}/users/${id}`);
    return response.data;
}

async function increaseVirtualMoney(userId: number,amount: number) {
    const response = await axios.put(`${environment.apiUrl}/users/${userId}/increase/virtual-money?amount=${amount}`);
    return response.data;
}


async function decreaseVirtualMoney(userId: number,amount: number) {
    const response = await axios.put(`${environment.apiUrl}/users/${userId}/decrease/virtual-money?amount=${amount}`);
    return response.data;
}

async function getTotalSpendingAndBudgetByCreator(creatorId: number) {
    const response = await axios.get(`${environment.apiUrl}/tasks/creator/${creatorId}/total-spending`);
    return response.data;
}

 const UserService = {
    getUsers,
    getUserById,
    increaseVirtualMoney,
    decreaseVirtualMoney,
    getTotalSpendingAndBudgetByCreator
}
export default UserService;