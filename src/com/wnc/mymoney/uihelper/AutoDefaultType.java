package com.wnc.mymoney.uihelper;

/**
 * 自动根据给定的数字匹配消费类型
 * 
 * @author wnc
 *
 */
public class AutoDefaultType
{
	static double cost;
	static int type_id;

	/**
	 * 看看是否匹配
	 * 
	 * @param cost1
	 * @param type_id1
	 * @return
	 */
	public static boolean isMatch(double cost1, int type_id1)
	{
		cost = cost1;
		type_id = type_id1;
		if (isTraffic() || isDining() || isTrainTicket() || isRoomCharge())
		{
			return true;
		}
		return false;
	}

	private static boolean isTraffic()
	{
		return type_id == 41 && (cost == 1.8 || cost == 2.7 || cost == 3.6 || cost == 4.5);
	}

	private static boolean isDining()
	{
		return type_id == 11 && (cost == 3 || cost == 4);
	}

	private static boolean isTrainTicket()
	{
		return type_id == 42 && (cost == 29 || cost == 16.5 || cost == 32);
	}

	private static boolean isRoomCharge()
	{
		return type_id == 33 && (cost == 770);
	}
}
