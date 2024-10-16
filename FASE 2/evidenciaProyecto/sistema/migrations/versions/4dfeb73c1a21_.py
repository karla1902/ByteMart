"""empty message

Revision ID: 4dfeb73c1a21
Revises: edb35d5a62c1
Create Date: 2024-10-03 00:32:53.750563

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '4dfeb73c1a21'
down_revision = 'edb35d5a62c1'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('producto', schema=None) as batch_op:
        batch_op.add_column(sa.Column('en_oferta', sa.Boolean(), nullable=True))

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('producto', schema=None) as batch_op:
        batch_op.drop_column('en_oferta')

    # ### end Alembic commands ###
