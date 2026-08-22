# Changelog

This document records the changes for each AntFlow release.

---

## [2.1.0] - 2026-08-22

Changes collected after commit `9ecb7a0feb5446c80d1b3111bf0b0feb428b174c`. Version bumped to **2.1.0**.

### ✨ New Features
- **Process classification config**: Support classifying and managing workflows.
- **APP management**: New mobile APP management module; fixed APP version field conflicting with MySQL and version switching issues.
- **Database management CRUD**: Added create/read/update/delete interfaces for database management.
- **Process permission management**: Added process permission management.
- **Department management APIs**: Added department management endpoints and optimized the department tree.
- **Process personnel/department/role management UI**: New visual configuration screens.
- **Business data view**: Added business data viewing, with optimized business data display.
- **Initiation page process items**: Added configurable process entries on the initiation page.
- **Node anti-recall**: Added the ability to prevent node recall.
- **Select approver by tag**: Support choosing approvers based on tags.
- **Node addition (countersign)**: Added node insertion with auto-node echo support.
- **Reject / return improvements**:
  - Optimized reject logic so a rejected process cannot be rejected again.
  - On disagreement, users can choose to end the process or return to a specified node.
  - Optimized countersign handling for hierarchical added nodes.
- **Batch approval**: Added batch approval with optimized code and a Y-axis scrollbar for the approval list.
- **Auto-approval settings**: Added a setting that allows users to configure automatic approval.

### 🐛 Bug Fixes
- Fixed date range parsing failure.
- Fixed external processes not being notified due to incorrect type-relation judgment.
- Fixed process disconnection when a process with added nodes was returned to an arbitrary node from the start.
- Fixed personnel error in forms.
- Fixed error caused by null values in newly added columns of the process template table.
- Fixed hierarchical added-node insertion not taking effect.
- Fixed added-node button permission issue (now inherited from the parent node).

### 🔧 Optimizations & Refactoring
- Optimized bpmnconf retrieval.
- Optimized department demo data regeneration and permission list queries.
- Removed deprecated code / outdated dependencies from the starter module.
- Restored the workflow approval feature.
- Adjusted `.gitignore`.
- Improved reject experience (no repeated reject after being rejected).

### 👥 Contributors
AntFlow, lidonghui, TylerZhou, 学费, cypress (skills)
