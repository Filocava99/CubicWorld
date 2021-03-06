package it.filippocavallari.cubicworld.data.block

import it.filippocavallari.cubicworld.data.blockstate.BlockState

data class State(val blockStates: BlockState, val currentVariant: String, val currentState: Int)