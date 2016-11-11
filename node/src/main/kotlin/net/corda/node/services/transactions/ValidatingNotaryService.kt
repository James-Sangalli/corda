package net.corda.node.services.transactions

import net.corda.core.crypto.Party
import net.corda.core.node.services.ServiceType
import net.corda.core.node.services.TimestampChecker
import net.corda.core.node.services.UniquenessProvider
import net.corda.node.services.api.ServiceHubInternal
import net.corda.protocols.ValidatingNotaryProtocol

/** A Notary service that validates the transaction chain of he submitted transaction before committing it */
class ValidatingNotaryService(services: ServiceHubInternal,
                              val timestampChecker: TimestampChecker,
                              val uniquenessProvider: UniquenessProvider) : NotaryService(services) {
    companion object {
        val type = ServiceType.notary.getSubType("validating")
    }

    override fun createProtocol(otherParty: Party): ValidatingNotaryProtocol {
        return ValidatingNotaryProtocol(otherParty, timestampChecker, uniquenessProvider)
    }
}