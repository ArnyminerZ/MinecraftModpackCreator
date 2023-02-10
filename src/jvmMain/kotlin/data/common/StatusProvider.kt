package data.common

import ui.components.FormDropdown

/**
 * Used by some UI components to provide the enabled status of an element.
 * @see FormDropdown
 */
interface StatusProvider {
    /** Whether the element should be enabled or not. */
    val enabled: Boolean
}