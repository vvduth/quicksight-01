# AWS Identity and Access Management (IAM) Security Project

A comprehensive cloud security project demonstrating AWS Identity and Access Management (IAM) best practices, including policy creation, user management, and access control implementation.

## 🎯 Project Overview

This project explores AWS IAM service to control authentication (who can sign in) and authorization (what permissions they have) in an AWS account. The focus is on implementing granular access controls using IAM policies, user groups, and testing security boundaries.

**Duration:** Approximately 1 hour  
**Difficulty Level:** Intermediate  
**Author:** Duke (ducthai)

## 🛠️ Tools and Technologies Used

### AWS Services
- **AWS IAM (Identity and Access Management)** - Core service for access control
- **Amazon EC2** - Target resources for policy testing
- **AWS Management Console** - Web interface for resource management

### Key Concepts Implemented
- **IAM Policies** - JSON rules that allow or deny actions on resources
- **Account Alias** - Friendly account name used in sign-in URLs
- **IAM Users** - Individual identities with credentials
- **User Groups** - Collections of users for simplified permission management
- **Resource Tagging** - Labels for resource identification and policy application

## 🏗️ Project Architecture

### Resource Structure
```
AWS Account (ducthai)
├── EC2 Instances
│   ├── Production Instance (nextwork-prod-duke)
│   │   └── Tags: Name=nextwork-prod-duke, Env=production
│   └── Development Instance (nextwork-dev-duke)
│       └── Tags: Name=nextwork-dev-duke, Env=development
├── IAM Components
│   ├── Custom JSON Policy (Development Access Policy)
│   ├── User Group (with policy attachment)
│   └── IAM User (member of user group)
└── Account Alias: ducthai
```

## 📋 Implementation Steps

### Step 1: Resource Tagging Strategy 🏷️

Implemented a comprehensive tagging strategy for EC2 instances:

**Production Instance:**
- `Name`: nextwork-prod-duke
- `Env`: production

**Development Instance:**
- `Name`: nextwork-dev-duke  
- `Env`: development

**Benefits of Tagging:**
- Resource identification and filtering
- Cost allocation tracking
- Environment-based policy application
- Simplified resource management

### Step 2: IAM Policy Creation 📜

Created a sophisticated JSON policy with granular permissions:

**Policy Objectives:**
- Allow all actions on EC2 instances tagged with `Env=development`
- Allow describe actions on all EC2 instances (read-only visibility)
- Deny all delete and create actions on all EC2 instances (safety measure)

**JSON Policy Structure:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "ec2:*",
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "ec2:ResourceTag/Env": "development"
        }
      }
    },
    {
      "Effect": "Allow",
      "Action": "ec2:Describe*",
      "Resource": "*"
    },
    {
      "Effect": "Deny",
      "Action": [
        "ec2:Delete*",
        "ec2:Create*"
      ],
      "Resource": "*"
    }
  ]
}
```

**Policy Components:**
- **Effect**: Allow or Deny permissions
- **Action**: Specific API calls or operations
- **Resource**: Target AWS resources (ARNs or "*" for all)
- **Condition**: Additional constraints based on tags or other attributes

### Step 3: Account Alias Configuration 🔗

**Setup Details:**
- Created friendly account alias: `ducthai`
- New sign-in URL: `https://ducthai.signin.aws.amazon.com/console`
- Implementation time: ~1 minute
- Benefit: User-friendly login experience instead of numeric account ID

### Step 4: User and Group Management 👥

**IAM User Group:**
- Created dedicated user group for development team
- Attached custom JSON policy to group
- Benefit: Simplified permission management for multiple users

**IAM User:**
- Created individual IAM user
- Added user to the development group
- Inherited group permissions automatically

### Step 5: Access Testing and Validation ✅

**Test Scenarios:**

**Production Instance Test:**
- Action: Attempted to stop production instance
- Result: ❌ **FAILED** - Access denied
- Reason: Policy doesn't allow actions on production-tagged resources
- UI Response: Error banner indicating unauthorized access

**Development Instance Test:**
- Action: Attempted to stop development instance  
- Result: ✅ **SUCCESS** - Action completed
- Reason: Policy allows all actions on development-tagged resources

## 🔐 Security Features Implemented

### 1. Principle of Least Privilege
- Users receive minimum necessary permissions
- Environment-based access restrictions
- Explicit deny rules for sensitive operations

### 2. Resource-Based Access Control
- Tag-based policy conditions
- Environment segregation (production vs development)
- Granular action permissions

### 3. Defense in Depth
- Multiple policy statements with different effects
- Explicit deny rules override allow rules
- Account-level and resource-level controls

## 📊 Project Outcomes

### Successfully Implemented
✅ **Tag-based resource organization**  
✅ **Custom IAM policy with conditional logic**  
✅ **Account alias for improved user experience**  
✅ **User group-based permission management**  
✅ **Policy testing and validation**  

### Key Learnings
- **Most Challenging**: Launching EC2 instances required subnet configuration
- **Most Rewarding**: Understanding subnet concepts and network fundamentals
- **Security Insight**: IAM users have limited access to account-wide features (history, billing, etc.)

## 🎓 Technical Skills Demonstrated

### AWS IAM Expertise
- JSON policy creation and syntax
- Conditional access controls
- Resource-based permissions
- User and group management

### Cloud Security Best Practices
- Least privilege access
- Environment segregation
- Tag-based governance
- Access testing methodologies

### Infrastructure Management
- EC2 instance tagging
- Network subnet configuration
- Account administration
- User experience optimization

## 🚨 Security Considerations

### Policy Design Principles
1. **Explicit Deny Rules** - Prevent accidental destructive actions
2. **Conditional Access** - Tag-based resource targeting
3. **Read-Only Baseline** - Default describe permissions for visibility
4. **Environment Isolation** - Separate production and development access

### Testing Methodology
1. **Positive Testing** - Verify allowed actions work correctly
2. **Negative Testing** - Confirm denied actions are blocked
3. **Boundary Testing** - Test edge cases and policy limits

## 🔄 Future Enhancements

### Advanced Features to Implement
- **Multi-Factor Authentication (MFA)** for enhanced security
- **Cross-account access** for complex organizational structures
- **Temporary credentials** using AWS STS
- **Policy simulation** for pre-deployment testing
- **CloudTrail integration** for audit logging

### Scaling Considerations
- **Role-based access** for service-to-service communication
- **Identity federation** for enterprise SSO integration
- **Automated policy management** using Infrastructure as Code
- **Compliance reporting** and governance frameworks

## 📚 Learning Resources

### AWS Documentation
- [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/)
- [IAM JSON Policy Reference](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies.html)
- [EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/)

### Best Practices
- [IAM Security Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [AWS Security Guidelines](https://aws.amazon.com/security/security-resources/)

## 🤝 Project Reflection

This AWS IAM project provided hands-on experience with cloud security fundamentals. The implementation of tag-based access controls demonstrates real-world security patterns used in enterprise environments. The most valuable learning was understanding how IAM policies cascade through user groups and the importance of testing security boundaries.

**Key Takeaway**: Effective cloud security requires both technical implementation and thorough testing to ensure policies work as intended.

---

**Project Source**: [NextWork.org](https://nextwork.org) - Cloud Security with AWS IAM  
**Sign-in URL**: https://ducthai.signin.aws.amazon.com/console  

*This project demonstrates practical AWS security implementation suitable for portfolio presentation and technical interviews.*