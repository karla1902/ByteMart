"""empty message

Revision ID: fb5053cc28b9
Revises: 15b9115eec5e
Create Date: 2024-10-19 15:41:04.913653

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'fb5053cc28b9'
down_revision = '15b9115eec5e'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('rol', schema=None) as batch_op:
        batch_op.drop_index('nombre')

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('rol', schema=None) as batch_op:
        batch_op.create_index('nombre', ['nombre'], unique=True)

    # ### end Alembic commands ###