package net.corda.core.flows

import co.paralleluniverse.fibers.Suspendable
import com.github.kittinunf.fuel.httpGet
import net.corda.core.identity.Party

/**
 * Created by sangalli on 1/8/17.
 */

object checkOpReturnFlow
{
    fun checkIfOpReturnTxIsPresent(txHash : String) : Boolean
    {
        val url = "https://opreturn.herokuapp.com/v2/readOpReturnTx/" + txHash
        val (request, response, result) = url.httpGet().responseString()
        return response.data.isNotEmpty()
    }

    //gets other nodes tx history and checks if they are submitted as OP Return txs in bitcoin testnet

    class Acceptor(val rcv: Party): FlowLogic<Unit>()
    {
        @Suspendable
        override fun call()
        {
            val txHashes = serviceHub.validatedTransactions.track().snapshot.map { it.id }.toTypedArray()
            send(rcv, txHashes)
        }
    }

    class Initiator(val rcv: Party) : FlowLogic<MutableMap<String, Boolean>>()
    {
        //get results back here
        @Suspendable
        override fun call() : MutableMap<String, Boolean>
        {
            val send = sendAndReceive<List<String>>(rcv, "Requesting your txs")
            println("this is the send: " + send)

            val txPresentInOpReturn = mutableMapOf<String, Boolean>()

            for (tx in send.data)
            {
                val isPresent = checkIfOpReturnTxIsPresent(tx)
                txPresentInOpReturn.put(tx, isPresent)
            }

            return txPresentInOpReturn
        }
    }
}