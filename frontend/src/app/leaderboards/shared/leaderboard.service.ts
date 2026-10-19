import axios from 'axios';
import { environment } from  '../../../environments/environment';


async function getAllTimeLeaderboard(){
    const response = await axios.get(`${environment.apiUrl}/leaderboard/all-time`);

    return response.data;
}

export const LeaderboardService = {
    getAllTimeLeaderboard
}
