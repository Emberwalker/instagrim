package io.drakon.uni.ac32007.instagrim.stores

import java.util.*

/**
 * A standard image comment.
 */
data class Comment(val pic: UUID, val user: String, val time: Date, val comment: String)